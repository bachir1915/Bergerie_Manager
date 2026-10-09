package com.bergerie.service;

import com.bergerie.enums.StatutAlerte;
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

        // Animaux
        stats.put("totalAnimaux", animalRepository.countByBergerieIdIn(bergerieIds));
        stats.put("animauxEnObservation", animalRepository.countByBergerieIdInAndStatutSante(bergerieIds, StatutSante.EN_OBSERVATION));
        stats.put("animauxMalades", animalRepository.countByBergerieIdInAndStatutSante(bergerieIds, StatutSante.EN_OBSERVATION));
        stats.put("animauxCritiques", animalRepository.countByBergerieIdInAndStatutSante(bergerieIds, StatutSante.CRITIQUE));
        stats.put("animauxSains", animalRepository.countByBergerieIdInAndStatutSante(bergerieIds, StatutSante.SAIN));

        // Alertes
        stats.put("alertesNonLues", alerteRepository.countByStatutAlerte(StatutAlerte.NOUVELLE));
        stats.put("dernieresAlertes", alerteRepository.findTop10ByOrderByDateHeureDesc());

        // Données environnementales
        stats.put("donneesMesures", donneesEnvRepository.findTop10ByOrderByDateHeureDesc());

        // Statistiques par race
        List<Object[]> parRace = animalRepository.countByRace();
        stats.put("statsRace", parRace);

        // Statistiques par santé
        List<Object[]> parSante = animalRepository.countByStatutSante();
        stats.put("statsSante", parSante);

        return stats;
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
        stats.put("dernieresAlertes", alerteRepository.findTop10ByOrderByDateHeureDesc());

        // Statistiques par santé
        stats.put("statsSante", animalRepository.countByStatutSante());

        return stats;
    }
}
