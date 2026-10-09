package com.bergerie.controller;

import com.bergerie.entity.DonneesEnvironnementales;
import com.bergerie.entity.Localisation;
import com.bergerie.service.DonneesEnvService;
import com.bergerie.service.LocalisationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private final com.bergerie.service.IotSimulationService iotSimulationService;
    private final com.bergerie.service.AnomalieDetectionService anomalieDetectionService;
    private final com.bergerie.client.MlPredictionClient mlPredictionClient;
    private final com.bergerie.repository.ZoneRepository zoneRepository;
    private final com.bergerie.repository.LocalisationRepository localisationRepository;

    /**
     * Réception données GPS depuis capteur Arduino
     * POST /api/iot/localisation
     */
    @PostMapping("/localisation")
    public ResponseEntity<?> recevoirLocalisation(@RequestBody LocalisationRequest request) {
        Localisation loc = localisationService.enregistrerPosition(
            request.getAnimalId(),
            request.getCapteurId(),
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

    /**
     * Statut de la simulation IoT
     * GET /api/iot/simulation/status
     */
    @org.springframework.web.bind.annotation.GetMapping("/simulation/status")
    public ResponseEntity<?> getSimulationStatus() {
        java.util.Map<String, Object> status = new java.util.HashMap<>();
        status.put("active", iotSimulationService.isSimulationActive());
        status.put("gpsTicks", iotSimulationService.getGpsTickCount());
        status.put("envTicks", iotSimulationService.getEnvTickCount());
        status.put("dernierGpsTick", iotSimulationService.getDernierGpsTick());
        status.put("dernierEnvTick", iotSimulationService.getDernierEnvTick());
        return ResponseEntity.ok(status);
    }

    /**
     * Activer/Désactiver la simulation en temps réel
     * POST /api/iot/simulation/toggle
     */
    @PostMapping("/simulation/toggle")
    public ResponseEntity<?> toggleSimulation() {
        boolean newState = iotSimulationService.toggleSimulation();
        return ResponseEntity.ok(java.util.Map.of(
            "active", newState,
            "message", newState ? "Simulation IoT activée" : "Simulation IoT suspendue"
        ));
    }

    /**
     * Déclencher un cycle immédiat de simulation (pour tests/démonstrations)
     * POST /api/iot/simulation/tick
     */
    @PostMapping("/simulation/tick")
    public ResponseEntity<?> declencherSimulationManuelle() {
        int gpsCount = iotSimulationService.simulerTickGps();
        int envCount = iotSimulationService.simulerTickEnv();
        return ResponseEntity.ok(java.util.Map.of(
            "status", "OK",
            "animauxSimules", gpsCount,
            "zonesSimulees", envCount
        ));
    }

    /**
     * Simuler une sortie de zone (déclenche immédiatement une alerte de géofencing)
     * POST /api/iot/simulation/sortie-zone
     */
    @PostMapping("/simulation/sortie-zone")
    public ResponseEntity<?> simulerSortieZone(@org.springframework.web.bind.annotation.RequestParam(required = false) Long animalId) {
        java.util.Map<String, Object> result = iotSimulationService.simulerSortieZone(animalId);
        return ResponseEntity.ok(result);
    }

    /**
     * Ramener tous les animaux (ou un animal spécifique) au centre sécurisé de leur zone
     * POST /api/iot/simulation/retour
     */
    @PostMapping("/simulation/retour")
    public ResponseEntity<?> simulerRetour(@org.springframework.web.bind.annotation.RequestParam(required = false) Long animalId) {
        java.util.Map<String, Object> result = iotSimulationService.simulerRetourAuBercail(animalId);
        return ResponseEntity.ok(result);
    }

    /**
     * Déclencher l'analyse prédictive ML (Isolation Forest) sur tous les animaux actifs
     * POST /api/iot/simulation/predict
     */
    @PostMapping("/simulation/predict")
    public ResponseEntity<?> declencherPrediction() {
        boolean mlOnline = mlPredictionClient.verifierSante();
        java.util.List<com.bergerie.dto.ml.PredictionAnomalieDTO> predictions =
                anomalieDetectionService.analyserTousAnimauxActifsAvecResultats();
        long anomalies = predictions.stream()
                .filter(p -> Boolean.TRUE.equals(p.getEstAnomalie()))
                .count();
        return ResponseEntity.ok(java.util.Map.of(
            "mlServiceOnline", mlOnline,
            "totalAnalyses", predictions.size(),
            "anomaliesDetectees", anomalies,
            "predictions", predictions
        ));
    }

    /**
     * Liste des zones pastorales (géofences) pour affichage sur la carte Leaflet
     * GET /api/iot/zones
     */
    @org.springframework.web.bind.annotation.GetMapping("/zones")
    public ResponseEntity<?> getZones() {
        var zonesDto = zoneRepository.findAll().stream()
                .map(com.bergerie.dto.ZoneDto::from)
                .toList();
        return ResponseEntity.ok(zonesDto);
    }

    /**
     * Historique récent des déplacements d'un animal pour tracer son parcours (trail)
     * GET /api/iot/localisation/animal/{animalId}/historique
     */
    @org.springframework.web.bind.annotation.GetMapping("/localisation/animal/{animalId}/historique")
    public ResponseEntity<?> getHistoriqueAnimal(@org.springframework.web.bind.annotation.PathVariable Long animalId) {
        var positions = localisationRepository.findByAnimalIdOrderByDateHeureDesc(animalId).stream()
                .limit(30)
                .map(com.bergerie.dto.LocalisationDto::from)
                .toList();
        return ResponseEntity.ok(positions);
    }

    // ---- DTOs internes ----

    @Data
    public static class LocalisationRequest {
        private Long animalId;
        private Long capteurId;
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
