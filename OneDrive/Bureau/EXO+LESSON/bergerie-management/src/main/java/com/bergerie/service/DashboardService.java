package com.bergerie.service;

import com.bergerie.entity.*;
import com.bergerie.enums.*;
import com.bergerie.repository.*;
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

    public Map<String, Object> getDashboardEleveur(Long bergerieId) {
        Map<String, Object> stats = new HashMap<>();

        // Animaux
        stats.put("totalAnimaux", animalRepository.countByBergerieId(bergerieId));
        stats.put("animauxMalades", animalRepository.countByBergerieIdAndStatutSante(bergerieId, StatutSante.MALADE));
        stats.put("animauxCritiques", animalRepository.countByBergerieIdAndStatutSante(bergerieId, StatutSante.CRITIQUE));
        stats.put("animauxSains", animalRepository.countByBergerieIdAndStatutSante(bergerieId, StatutSante.BON));

        // Alertes
        stats.put("alertesNonLues", alerteRepository.countByStatutAlerte(StatutAlerte.NON_LUE));
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
        stats.put("animauxMalades", animalRepository.countByStatutSante(StatutSante.MALADE));
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
