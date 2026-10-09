package com.bergerie.service;

import com.bergerie.dto.ActeForm;
import com.bergerie.dto.AlerteDto;
import com.bergerie.dto.DonneeEnvDto;
import com.bergerie.dto.EspaceVeterinaireResponse;
import com.bergerie.entity.ActeMedical;
import com.bergerie.entity.Animal;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.Veterinaire;
import com.bergerie.enums.StatutSante;
import com.bergerie.enums.TypeSoin;
import com.bergerie.repository.ActeMedicalRepository;
import com.bergerie.repository.AlerteRepository;
import com.bergerie.repository.AnimalRepository;
import com.bergerie.repository.BergerieRepository;
import com.bergerie.repository.DonneesEnvRepository;
import com.bergerie.repository.VeterinaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Construit la vue de l'espace vétérinaire focalisée sur une bergerie.
 */
@Service
@RequiredArgsConstructor
public class EspaceVeterinaireService {

    /** Clé de session HTTP portant l'id de la bergerie consultée par le vétérinaire. */
    public static final String SESSION_BERGERIE = "bergerieCouranteId";

    private static final int LIMITE_ACTES = 20;
    private static final int LIMITE_MESURES = 20;

    private final BergerieRepository bergerieRepository;
    private final AnimalRepository animalRepository;
    private final AlerteRepository alerteRepository;
    private final ActeMedicalRepository acteMedicalRepository;
    private final DonneesEnvRepository donneesEnvRepository;
    private final VeterinaireRepository veterinaireRepository;

    /** Résout une bergerie à partir de son code (utilisé à la connexion et au changement de bergerie). */
    @Transactional(readOnly = true)
    public Bergerie parCodeOuErreur(String code) {
        String normalise = BergerieCodeService.normaliser(code);
        if (!BergerieCodeService.estValide(normalise)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Code de bergerie invalide (format attendu : AA-76-VI-77).");
        }
        return bergerieRepository.findByCode(normalise)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aucune bergerie ne correspond à ce code."));
    }

    @Transactional(readOnly = true)
    public EspaceVeterinaireResponse construire(Long bergerieId) {
        Bergerie b = bergerieRepository.findById(bergerieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bergerie introuvable."));

        var animaux = animalRepository.findByBergerieId(bergerieId).stream()
                .map(a -> new EspaceVeterinaireResponse.AnimalItem(
                        a.getId(), a.getNom(), a.getRace(), a.getSexe(), a.getPoids(),
                        a.getDateNaissance(), a.getStatut().name(), a.getStatutSante().name(), a.getNumeroCapteur()))
                .toList();

        var alertes = alerteRepository.findByBergerieId(bergerieId).stream()
                .map(al -> new AlerteDto(al.getId(), al.getType().name(), al.getMessage(),
                        al.getNiveau().name(), al.getStatutAlerte().name(), al.getDateHeure(),
                        al.getAnimal() != null ? al.getAnimal().getNom() : null))
                .toList();

        var actes = acteMedicalRepository.findByBergerieId(bergerieId).stream()
                .limit(LIMITE_ACTES)
                .map(ac -> new EspaceVeterinaireResponse.ActeItem(
                        ac.getId(),
                        ac.getAnimal() != null ? ac.getAnimal().getNom() : null,
                        ac.getTypeSoin().name(), ac.getDescription(), ac.getMedicament(), ac.getDose(),
                        ac.getDateSoin(), ac.getProchainRappel()))
                .toList();

        var mesures = donneesEnvRepository.findByBergerieId(bergerieId).stream()
                .limit(LIMITE_MESURES)
                .map(d -> new DonneeEnvDto(d.getId(),
                        d.getZone() != null ? d.getZone().getNom() : null,
                        d.getTemperature(), d.getHumidite(), d.getQualiteAir(), d.getDateHeure()))
                .toList();

        long malades = animalRepository.countByBergerieIdAndStatutSante(bergerieId, StatutSante.EN_OBSERVATION);
        long critiques = animalRepository.countByBergerieIdAndStatutSante(bergerieId, StatutSante.CRITIQUE);

        var eleveur = b.getEleveur();
        var info = new EspaceVeterinaireResponse.BergerieInfo(
                b.getId(), b.getNom(), b.getCode(), b.getRegion(), b.getAdresse(), b.getSuperficieTotale(),
                eleveur != null ? eleveur.getPrenom() + " " + eleveur.getNom() : null,
                eleveur != null ? eleveur.getTelephone() : null,
                eleveur != null ? eleveur.getEmail() : null,
                b.getZones() != null ? b.getZones().stream().map(z -> z.getNom()).toList() : List.of());

        return new EspaceVeterinaireResponse(
                info, animaux.size(), malades, critiques, animaux, alertes, actes, mesures);
    }

    /**
     * Le vétérinaire enregistre un acte médical pour un animal de la bergerie consultée.
     * @return l'espace mis à jour (avec le nouvel acte en tête de l'historique).
     */
    @Transactional
    public EspaceVeterinaireResponse ajouterActe(Long bergerieId, String emailVet, ActeForm form) {
        Veterinaire vet = veterinaireRepository.findByEmail(emailVet)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vétérinaire inconnu."));

        Animal animal = animalRepository.findById(form.animalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal introuvable."));
        if (animal.getBergerie() == null || !animal.getBergerie().getId().equals(bergerieId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Cet animal n'appartient pas à la bergerie que vous consultez.");
        }

        TypeSoin type;
        try {
            type = TypeSoin.valueOf(form.typeSoin());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type de soin invalide.");
        }

        ActeMedical acte = new ActeMedical();
        acte.setAnimal(animal);
        acte.setUtilisateur(vet);
        acte.setTypeSoin(type);
        acte.setDescription(form.description());
        acte.setMedicament(form.medicament());
        acte.setDose(form.dose());
        acte.setDateSoin(form.dateSoin());
        acte.setProchainRappel(form.prochainRappel());
        acteMedicalRepository.save(acte);

        return construire(bergerieId);
    }
}
