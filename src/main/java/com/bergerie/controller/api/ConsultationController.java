package com.bergerie.controller.api;

import com.bergerie.dto.AlerteDto;
import com.bergerie.dto.AnimalDto;
import com.bergerie.dto.DonneeEnvDto;
import com.bergerie.dto.LocalisationDto;
import com.bergerie.entity.Animal;
import com.bergerie.entity.Bergerie;
import com.bergerie.util.Affichage;
import com.bergerie.entity.Eleveur;
import com.bergerie.entity.Utilisateur;
import com.bergerie.repository.AlerteRepository;
import com.bergerie.repository.AnimalRepository;
import com.bergerie.repository.DonneesEnvRepository;
import com.bergerie.repository.LocalisationRepository;
import com.bergerie.repository.UtilisateurRepository;
import com.bergerie.enums.StatutAlerte;
import com.bergerie.service.ConnexionService;
import com.bergerie.service.EspaceVeterinaireService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Listes de consultation (animaux, alertes, mesures) filtrées selon le rôle :
 *   - ELEVEUR     : les bergeries qu'il possède
 *   - VETERINAIRE : la bergerie du code utilisé à la connexion
 *   - ADMIN       : tout
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ELEVEUR', 'VETERINAIRE', 'ADMIN')")
public class ConsultationController {

    private final UtilisateurRepository utilisateurRepository;
    private final AnimalRepository animalRepository;
    private final AlerteRepository alerteRepository;
    private final DonneesEnvRepository donneesEnvRepository;
    private final LocalisationRepository localisationRepository;
    private final com.bergerie.repository.ZoneRepository zoneRepository;
    private final ConnexionService connexionService;

    @GetMapping("/animaux")
    @Transactional(readOnly = true)
    public List<AnimalDto> animaux(Authentication auth, HttpSession session) {
        List<Long> ids = portee(auth, session);
        return animauxVisibles(ids).stream().map(AnimalDto::from).toList();
    }

    @GetMapping("/alertes")
    @Transactional(readOnly = true)
    public List<AlerteDto> alertes(Authentication auth, HttpSession session) {
        List<Long> ids = portee(auth, session);
        var alertes = Affichage.alertes((ids == null)
                ? alerteRepository.findTop10ByOrderByDateHeureDesc()
                : alerteRepository.findByBergerieIds(ids));
        return alertes.stream()
                .map(a -> new AlerteDto(a.getId(), a.getType().name(), a.getMessage(),
                        a.getNiveau().name(), a.getStatutAlerte().name(), a.getDateHeure(),
                        a.getAnimal() != null ? a.getAnimal().getNom() : null))
                .toList();
    }

    /** Marque une alerte comme traitée (éleveur ou vétérinaire, dans sa portée). */
    @PostMapping("/alertes/{id}/traiter")
    @PreAuthorize("hasAnyRole('ELEVEUR', 'VETERINAIRE')")
    @Transactional
    public AlerteDto traiterAlerte(@PathVariable Long id, Authentication auth, HttpSession session) {
        var a = alerteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerte introuvable."));
        List<Long> ids = portee(auth, session);
        Long bergerieId = (a.getAnimal() != null && a.getAnimal().getBergerie() != null)
                ? a.getAnimal().getBergerie().getId() : null;
        if (ids != null && (bergerieId == null || !ids.contains(bergerieId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette alerte ne concerne pas vos bergeries.");
        }
        a.setStatutAlerte(StatutAlerte.TRAITEE);
        alerteRepository.save(a);
        return new AlerteDto(a.getId(), a.getType().name(), a.getMessage(), a.getNiveau().name(),
                a.getStatutAlerte().name(), a.getDateHeure(), a.getAnimal() != null ? a.getAnimal().getNom() : null);
    }

    @GetMapping("/donnees-env")
    @Transactional(readOnly = true)
    public List<DonneeEnvDto> donneesEnv(Authentication auth, HttpSession session) {
        List<Long> ids = portee(auth, session);
        var mesures = (ids == null)
                ? donneesEnvRepository.findTop10ByOrderByDateHeureDesc()
                : donneesEnvRepository.findByBergerieIds(ids);
        return mesures.stream()
                .map(d -> new DonneeEnvDto(d.getId(),
                        d.getZone() != null ? d.getZone().getNom() : null,
                        d.getTemperature(), d.getHumidite(), d.getQualiteAir(), d.getDateHeure()))
                .toList();
    }

    /**
     * Dernière position GPS connue de chaque animal, filtrée par portée de l'utilisateur.
     * Utilisée par la carte Leaflet Angular pour afficher les marqueurs.
     */
    @GetMapping("/localisation")
    @Transactional(readOnly = true)
    public List<LocalisationDto> dernieresPositions(Authentication auth, HttpSession session) {
        List<Long> ids = portee(auth, session);
        // Seuls les animaux affichés dans la liste (5 au maximum) apparaissent sur la carte.
        var visibles = animauxVisibles(ids).stream().map(Animal::getId).toList();
        var positions = localisationRepository.findLatestForAllAnimals().stream()
                .filter(loc -> visibles.contains(loc.getAnimal().getId()))
                .toList();
        return positions.stream().map(LocalisationDto::from).toList();
    }

    /**
     * Liste des zones pastorales (géofences) visibles par l'utilisateur.
     */
    @GetMapping("/localisation/zones")
    @Transactional(readOnly = true)
    public List<com.bergerie.dto.ZoneDto> zones(Authentication auth, HttpSession session) {
        List<Long> ids = portee(auth, session);
        var zones = zoneRepository.findAll();
        if (ids != null) {
            zones = zones.stream()
                    .filter(z -> z.getBergerie() != null && ids.contains(z.getBergerie().getId()))
                    .toList();
        }
        return zones.stream().map(com.bergerie.dto.ZoneDto::from).toList();
    }

    /**
     * Historique récent des déplacements d'un animal pour le tracé sur la carte.
     */
    @GetMapping("/localisation/animal/{animalId}")
    @Transactional(readOnly = true)
    public List<LocalisationDto> historiqueAnimal(@PathVariable Long animalId, Authentication auth, HttpSession session) {
        List<Long> ids = portee(auth, session);
        var animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal introuvable."));
        if (ids != null && (animal.getBergerie() == null || !ids.contains(animal.getBergerie().getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cet animal ne fait pas partie de vos bergeries.");
        }
        return localisationRepository.findByAnimalIdOrderByDateHeureDesc(animalId).stream()
                .limit(30)
                .map(LocalisationDto::from)
                .toList();
    }

    /** Animaux visibles par l'utilisateur, plafonnés à {@link Affichage#MAX}. */
    private List<Animal> animauxVisibles(List<Long> ids) {
        return Affichage.animaux((ids == null)
                ? animalRepository.findAll()
                : animalRepository.findByBergerieIdInOrderByNomAsc(ids));
    }

    /** @return null = toutes les bergeries (admin) ; sinon la liste des ids visibles. */
    private List<Long> portee(Authentication auth, HttpSession session) {
        Utilisateur u = utilisateurRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur inconnu."));
        return switch (u.getRole()) {
            case ADMIN -> null;
            case ELEVEUR -> ((Eleveur) u).getBergeries().stream().map(Bergerie::getId).toList();
            case VETERINAIRE -> {
                Object b = session.getAttribute(EspaceVeterinaireService.SESSION_BERGERIE);
                // La connexion doit rester acceptée par l'éleveur (il peut l'annuler à tout moment).
                yield (b == null || !connexionService.estAcceptee(u.getEmail(), (Long) b))
                        ? List.<Long>of() : List.of((Long) b);
            }
        };
    }
}
