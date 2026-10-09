package com.bergerie.controller.api;

import com.bergerie.dto.AlerteDto;
import com.bergerie.dto.DashboardEleveurResponse;
import com.bergerie.dto.DashboardVeterinaireResponse;
import com.bergerie.dto.DonneeEnvDto;
import com.bergerie.dto.StatEntry;
import com.bergerie.entity.Alerte;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.DonneesEnvironnementales;
import com.bergerie.entity.Eleveur;
import com.bergerie.repository.EleveurRepository;
import com.bergerie.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final DashboardService dashboardService;
    private final EleveurRepository eleveurRepository;

    @GetMapping("/eleveur")
    public DashboardEleveurResponse eleveur(Authentication authentication) {
        Eleveur eleveur = eleveurRepository.findByEmail(authentication.getName()).orElseThrow();
        List<Long> bergerieIds = eleveur.getBergeries().stream().map(Bergerie::getId).toList();
        Map<String, Object> stats = dashboardService.getDashboardEleveur(bergerieIds);

        return new DashboardEleveurResponse(
            (long) stats.get("totalAnimaux"),
            (long) stats.get("animauxSains"),
            (long) stats.get("animauxMalades"),
            (long) stats.get("animauxCritiques"),
            (long) stats.get("alertesNonLues"),
            toAlerteDtos((List<Alerte>) stats.get("dernieresAlertes")),
            toDonneeEnvDtos((List<DonneesEnvironnementales>) stats.get("donneesMesures")),
            toStatEntries((List<Object[]>) stats.get("statsSante")),
            toStatEntries((List<Object[]>) stats.get("statsRace"))
        );
    }

    @GetMapping("/veterinaire")
    public DashboardVeterinaireResponse veterinaire() {
        Map<String, Object> stats = dashboardService.getDashboardVeterinaire();

        return new DashboardVeterinaireResponse(
            (long) stats.get("totalAnimaux"),
            (long) stats.get("animauxMalades"),
            (long) stats.get("animauxCritiques"),
            (long) stats.get("totalVaccinations"),
            (long) stats.get("totalTraitements"),
            (long) stats.get("totalAnalyses"),
            (long) stats.get("totalConsultations"),
            toAlerteDtos((List<Alerte>) stats.get("dernieresAlertes")),
            toStatEntries((List<Object[]>) stats.get("statsSante"))
        );
    }

    private List<AlerteDto> toAlerteDtos(List<Alerte> alertes) {
        return alertes.stream()
            .map(a -> new AlerteDto(
                a.getId(),
                a.getType().name(),
                a.getMessage(),
                a.getNiveau().name(),
                a.getStatutAlerte().name(),
                a.getDateHeure(),
                a.getAnimal() != null ? a.getAnimal().getNom() : null
            ))
            .toList();
    }

    private List<DonneeEnvDto> toDonneeEnvDtos(List<DonneesEnvironnementales> donnees) {
        return donnees.stream()
            .map(d -> new DonneeEnvDto(
                d.getId(),
                d.getZone() != null ? d.getZone().getNom() : null,
                d.getTemperature(),
                d.getHumidite(),
                d.getQualiteAir(),
                d.getDateHeure()
            ))
            .toList();
    }

    private List<StatEntry> toStatEntries(List<Object[]> rows) {
        return rows.stream()
            .map(row -> new StatEntry(String.valueOf(row[0]), (Long) row[1]))
            .toList();
    }
}
