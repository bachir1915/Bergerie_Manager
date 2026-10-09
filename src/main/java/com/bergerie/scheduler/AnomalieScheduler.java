package com.bergerie.scheduler;

import com.bergerie.service.AnomalieDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tâche planifiée périodique pour la surveillance automatique de la santé
 * et du comportement du troupeau via le microservice IA.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "ml.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class AnomalieScheduler {

    private final AnomalieDetectionService anomalieDetectionService;

    /**
     * Exécute l'analyse d'anomalies toutes les heures pour tous les animaux actifs.
     * La périodicité est configurable dans application.properties (ml.analysis.cron).
     */
    @Scheduled(cron = "${ml.analysis.cron:0 0 * * * *}")
    public void executerAnalyseHoraire() {
        log.info("[SCHEDULER ML] Lancement automatique de l'analyse horaire du troupeau...");
        try {
            anomalieDetectionService.analyserTousAnimauxActifs();
        } catch (Exception e) {
            log.error("[SCHEDULER ML] Erreur globale lors de l'exécution du scheduler : {}", e.getMessage(), e);
        }
    }
}
