package com.bergerie.service;

import com.bergerie.entity.Alerte;
import com.bergerie.entity.Animal;
import com.bergerie.enums.StatutAlerte;
import com.bergerie.util.Affichage;
import java.util.stream.Collectors;
import com.bergerie.enums.StatutSante;
import com.bergerie.enums.TypeAlerte;
import com.bergerie.enums.TypeSoin;
import com.bergerie.repository.ActeMedicalRepository;
import com.bergerie.repository.AlerteRepository;
import com.bergerie.repository.AnimalRepository;
import com.bergerie.repository.DonneesEnvRepository;
import com.bergerie.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AnimalRepository animalRepository;
    private final AlerteRepository alerteRepository;
    private final ActeMedicalRepository acteMedicalRepository;
    private final DonneesEnvRepository donneesEnvRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Map<String, Object> getDashboardEleveur(List<Long> bergerieIds) {
        Map<String, Object> stats = new HashMap<>();

        // Animaux : au plus 5 animaux affichés ; les statistiques portent sur ces animaux.
        List<Animal> animaux = Affichage.animaux(animalRepository.findByBergerieIdInOrderByNomAsc(bergerieIds));
        stats.put("totalAnimaux", (long) animaux.size());
        stats.put("animauxEnObservation", compter(animaux, StatutSante.EN_OBSERVATION));
        stats.put("animauxMalades", compter(animaux, StatutSante.EN_OBSERVATION));
        stats.put("animauxCritiques", compter(animaux, StatutSante.CRITIQUE));
        stats.put("animauxSains", compter(animaux, StatutSante.SAIN));

        // Alertes : les 5 plus récentes des bergeries de l'éleveur.
        List<Alerte> alertes = Affichage.alertes(alerteRepository.findByBergerieIds(bergerieIds));
        stats.put("alertesNonLues", alertes.stream().filter(a -> a.getStatutAlerte() == StatutAlerte.NOUVELLE).count());
        stats.put("dernieresAlertes", alertes);

        // Données environnementales
        stats.put("donneesMesures", donneesEnvRepository.findTop10ByOrderByDateHeureDesc());

        // Statistiques par race
        stats.put("statsRace", repartition(animaux.stream().map(Animal::getRace).toList()));

        // Statistiques par santé
        stats.put("statsSante", repartition(animaux.stream().map(a -> String.valueOf(a.getStatutSante())).toList()));

        return stats;
    }

    private long compter(List<Animal> animaux, StatutSante sante) {
        return animaux.stream().filter(a -> a.getStatutSante() == sante).count();
    }

    /** Répartition [valeur, effectif] d'une liste de valeurs (même forme que les requêtes de comptage). */
    private List<Object[]> repartition(List<String> valeurs) {
        return valeurs.stream()
                .collect(Collectors.groupingBy(v -> v == null ? "—" : v, java.util.LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new Object[]{e.getKey(), e.getValue()})
                .toList();
    }

    public Map<String, Object> getDashboardVeterinaire() {
        Map<String, Object> stats = new HashMap<>();

        // Animaux
        stats.put("totalAnimaux", animalRepository.count());
        stats.put("animauxEnObservation", animalRepository.countByStatutSante(StatutSante.EN_OBSERVATION));
        stats.put("animauxMalades", animalRepository.countByStatutSante(StatutSante.EN_OBSERVATION));
        stats.put("animauxCritiques", animalRepository.countByStatutSante(StatutSante.CRITIQUE));

        // Actes médicaux
        stats.put("totalVaccinations", acteMedicalRepository.countByTypeSoin(TypeSoin.VACCINATION));
        stats.put("totalTraitements", acteMedicalRepository.countByTypeSoin(TypeSoin.TRAITEMENT));
        stats.put("totalAnalyses", acteMedicalRepository.countByTypeSoin(TypeSoin.ANALYSE));
        stats.put("totalConsultations", acteMedicalRepository.countByTypeSoin(TypeSoin.CONSULTATION));

        // Alertes sanitaires
        stats.put("alertesSanteCritiques",
            alerteRepository.findByTypeOrderByDateHeureDesc(TypeAlerte.ANOMALIE_SANTE));
        stats.put("dernieresAlertes", Affichage.alertes(alerteRepository.findTop10ByOrderByDateHeureDesc()));

        // Statistiques par santé
        stats.put("statsSante", animalRepository.countByStatutSante());

        return stats;
    }
}
