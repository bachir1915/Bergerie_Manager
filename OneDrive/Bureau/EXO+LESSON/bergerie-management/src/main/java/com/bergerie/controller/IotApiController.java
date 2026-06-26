package com.bergerie.controller;

import com.bergerie.entity.DonneesEnvironnementales;
import com.bergerie.entity.Localisation;
import com.bergerie.service.DonneesEnvService;
import com.bergerie.service.LocalisationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API REST pour le Système IoT (Arduino + capteurs GPS/DHT22)
 * Ces endpoints seront appelés par les modules Arduino via HTTP POST
 */
@RestController
@RequestMapping("/api/iot")
@RequiredArgsConstructor
public class IotApiController {

    private final LocalisationService localisationService;
    private final DonneesEnvService donneesEnvService;

    /**
     * Réception données GPS depuis capteur Arduino
     * POST /api/iot/localisation
     */
    @PostMapping("/localisation")
    public ResponseEntity<?> recevoirLocalisation(@RequestBody LocalisationRequest request) {
        Localisation loc = localisationService.enregistrerPosition(
            request.getAnimalId(),
            request.getLatitude(),
            request.getLongitude(),
            request.getVitesse(),
            request.getPrecisionGps()
        );
        return ResponseEntity.ok().body("{\"status\":\"OK\",\"id\":" + loc.getId() + "}");
    }

    /**
     * Réception données environnementales depuis capteur DHT22 Arduino
     * POST /api/iot/donnees-env
     */
    @PostMapping("/donnees-env")
    public ResponseEntity<?> recevoirDonneesEnv(@RequestBody DonneesEnvRequest request) {
        DonneesEnvironnementales d = donneesEnvService.enregistrerDonnees(
            request.getZoneId(),
            request.getCapteurId(),
            request.getTemperature(),
            request.getHumidite(),
            request.getQualiteAir()
        );
        return ResponseEntity.ok().body("{\"status\":\"OK\",\"id\":" + d.getId() + "}");
    }

    // ---- DTOs internes ----

    @Data
    public static class LocalisationRequest {
        private Long animalId;
        private Double latitude;
        private Double longitude;
        private Double vitesse;
        private Double precisionGps;
    }

    @Data
    public static class DonneesEnvRequest {
        private Long zoneId;
        private Long capteurId;
        private Double temperature;
        private Double humidite;
        private Double qualiteAir;
    }
}
