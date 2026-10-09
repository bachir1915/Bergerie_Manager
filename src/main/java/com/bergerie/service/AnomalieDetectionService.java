package com.bergerie.service;

import com.bergerie.client.MlPredictionClient;
import com.bergerie.dto.ml.CapteurLectureDTO;
import com.bergerie.dto.ml.PredictionAnomalieDTO;
import com.bergerie.entity.Alerte;
import com.bergerie.entity.Animal;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.DonneesEnvironnementales;
import com.bergerie.entity.Eleveur;
import com.bergerie.entity.Localisation;
import com.bergerie.entity.Notification;
import com.bergerie.entity.Veterinaire;
import com.bergerie.enums.CanalNotif;
import com.bergerie.enums.NiveauAlerte;
import com.bergerie.enums.StatutAlerte;
import com.bergerie.enums.StatutAnimal;
import com.bergerie.enums.StatutCapteur;
import com.bergerie.enums.StatutSante;
import com.bergerie.enums.StatutValidation;
import com.bergerie.enums.TypeAlerte;
import com.bergerie.repository.AlerteRepository;
import com.bergerie.repository.AnimalRepository;
import com.bergerie.repository.DonneesEnvRepository;
import com.bergerie.repository.LocalisationRepository;
import com.bergerie.repository.NotificationRepository;
import com.bergerie.repository.VeterinaireRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service métier analysant les données agrégées ESP32 de chaque animal sur la dernière heure,
 * sollicitant le microservice Python (Isolation Forest) et créant des alertes/notifications
 * ciblées en cas d'anomalie détectée.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalieDetectionService {

    private final AnimalRepository animalRepository;
    private final LocalisationRepository localisationRepository;
    private final DonneesEnvRepository donneesEnvRepository;
    private final AlerteRepository alerteRepository;
    private final NotificationRepository notificationRepository;
    private final VeterinaireRepository veterinaireRepository;
    private final MlPredictionClient mlPredictionClient;

    /**
     * Analyse l'ensemble des animaux actifs du troupeau sur la dernière heure.
     * Les erreurs réseau ou l'indisponibilité du microservice Python sont gérées sans interrompre
     * le traitement des autres animaux.
     */
    public void analyserTousAnimauxActifs() {
        log.info("--- [ANALYSE ML] Début de l'analyse comportementale horaire du troupeau ---");
        List<Animal> animauxActifs = animalRepository.findByStatut(StatutAnimal.ACTIF);

        if (animauxActifs.isEmpty()) {
            log.info("[ANALYSE ML] Aucun animal actif à analyser.");
            return;
        }

        int succes = 0;
        int anomaliesDetectees = 0;
        LocalDateTime maintenant = LocalDateTime.now();

        for (Animal animal : animauxActifs) {
            try {
                Optional<PredictionAnomalieDTO> resultat = analyserAnimal(animal, maintenant);
                if (resultat.isPresent()) {
                    succes++;
                    if (Boolean.TRUE.equals(resultat.get().getEstAnomalie())) {
                        anomaliesDetectees++;
                    }
                }
            } catch (Exception e) {
                log.error("[ANALYSE ML] Échec inattendu lors de l'analyse de l'animal ID {} ({}): {}",
                        animal.getId(), animal.getNom(), e.getMessage());
            }
        }

        log.info("--- [ANALYSE ML] Fin de l'analyse : {}/{} animaux évalués avec succès, {} anomalie(s) détectée(s) ---",
                succes, animauxActifs.size(), anomaliesDetectees);
    }

    /**
     * Analyse tous les animaux actifs et retourne les prédictions (utile pour API / simulation / tests).
     */
    public java.util.List<PredictionAnomalieDTO> analyserTousAnimauxActifsAvecResultats() {
        log.info("--- [ANALYSE ML DEMANDEE] Analyse comportementale du troupeau ---");
        java.util.List<PredictionAnomalieDTO> resultats = new java.util.ArrayList<>();
        List<Animal> animauxActifs = animalRepository.findByStatut(StatutAnimal.ACTIF);
        if (animauxActifs.isEmpty()) {
            return resultats;
        }

        LocalDateTime maintenant = LocalDateTime.now();
        for (Animal animal : animauxActifs) {
            try {
                Optional<PredictionAnomalieDTO> res = analyserAnimal(animal, maintenant);
                res.ifPresent(resultats::add);
            } catch (Exception e) {
                log.error("[ANALYSE ML] Échec lors de l'analyse de l'animal #{}: {}", animal.getId(), e.getMessage());
            }
        }
        return resultats;
    }

    /**
     * Agrège les capteurs ESP32 de la dernière heure pour un animal et interroge le microservice Python.
     * Filtre les capteurs dont le statut n'est pas ACTIF (ignore HORS_SERVICE, EN_MAINTENANCE...).
     *
     * @param animal L'animal à inspecter
     * @param maintenant Horodatage de référence
     * @return Prédiction retournée par le modèle ML, ou Optional.empty() si indisponible
     */
    @Transactional
    public Optional<PredictionAnomalieDTO> analyserAnimal(Animal animal, LocalDateTime maintenant) {
        LocalDateTime debut = maintenant.minusHours(1);

        // 1. Calcul des métriques de mouvement à partir des positions GPS (capteurs ACTIF uniquement)
        List<Localisation> toutesPositions = localisationRepository
                .findByAnimalIdAndDateHeureAfterOrderByDateHeureAsc(animal.getId(), debut);

        List<Localisation> positions = toutesPositions.stream()
                .filter(p -> p.getCapteur() == null || p.getCapteur().getStatut() == StatutCapteur.ACTIF)
                .toList();

        double distanceKm = calculerDistanceTotaleKm(positions);
        double vitesseMoyenneKmh = calculerVitesseMoyenneKmh(positions, distanceKm);

        // 2. Récupération des mesures environnementales de la bergerie (capteurs ACTIF uniquement)
        Bergerie bergerie = animal.getBergerie();
        double tempAmbiante = 22.0; // Valeur par défaut si aucun capteur environnemental actif
        double humidite = 60.0;
        double qualiteAir = 30.0;

        if (bergerie != null) {
            List<DonneesEnvironnementales> toutesDonneesEnv = donneesEnvRepository
                    .findRecentByBergerieId(bergerie.getId(), debut);

            List<DonneesEnvironnementales> donneesEnv = toutesDonneesEnv.stream()
                    .filter(d -> d.getCapteur() == null || d.getCapteur().getStatut() == StatutCapteur.ACTIF)
                    .toList();

            if (!donneesEnv.isEmpty()) {
                tempAmbiante = donneesEnv.stream()
                        .mapToDouble(DonneesEnvironnementales::getTemperature)
                        .average().orElse(22.0);
                humidite = donneesEnv.stream()
                        .mapToDouble(DonneesEnvironnementales::getHumidite)
                        .average().orElse(60.0);
                qualiteAir = donneesEnv.stream()
                        .filter(d -> d.getQualiteAir() != null)
                        .mapToDouble(DonneesEnvironnementales::getQualiteAir)
                        .average().orElse(30.0);
            }
        }

        // 3. Construction du DTO à destination du microservice FastAPI
        CapteurLectureDTO lecture = CapteurLectureDTO.builder()
                .animalId(animal.getId())
                .heureJour(maintenant.getHour())
                .temperatureAmbiante(Math.round(tempAmbiante * 10.0) / 10.0)
                .humidite(Math.round(humidite * 10.0) / 10.0)
                .qualiteAir(Math.round(qualiteAir * 10.0) / 10.0)
                .distanceParcourueKm(Math.round(distanceKm * 1000.0) / 1000.0)
                .vitesseMoyenneKmh(Math.round(vitesseMoyenneKmh * 100.0) / 100.0)
                .build();

        // 4. Appel HTTP WebClient vers Python
        Optional<PredictionAnomalieDTO> predictionOpt = mlPredictionClient.predireAnomalie(lecture);

        if (predictionOpt.isEmpty()) {
            return Optional.empty();
        }

        PredictionAnomalieDTO prediction = predictionOpt.get();

        // 5. Déclenchement de l'Alerte et des Notifications si anomalie confirmée
        if (Boolean.TRUE.equals(prediction.getEstAnomalie())) {
            traiterAnomalieDetectee(animal, lecture, prediction, maintenant);
        }

        return Optional.of(prediction);
    }

    /**
     * Traite une anomalie confirmée par l'Isolation Forest :
     * - Crée UNE SEULE Alerte
     * - Crée une Notification pour l'éleveur propriétaire
     * - Crée une Notification par vétérinaire validé
     * - Met à jour le statut de santé de l'animal si critique/élevé
     */
    private void traiterAnomalieDetectee(Animal animal, CapteurLectureDTO lecture,
                                        PredictionAnomalieDTO prediction, LocalDateTime maintenant) {

        NiveauAlerte niveau = mapperNiveauAlerte(prediction.getNiveauRisque());
        String details = prediction.getDetails() != null ? prediction.getDetails() : "Comportement atypique détecté";

        String messageAlerte = String.format(
                "[VIGILANCE SANTÉ] Constat %s — '%s' : %s (Activité: %.2f km, Vitesse: %.1f km/h, Temp. enclos: %.1f°C)",
                prediction.getNiveauRisque(),
                animal.getNom(),
                details,
                lecture.getDistanceParcourueKm(),
                lecture.getVitesseMoyenneKmh(),
                lecture.getTemperatureAmbiante()
        );

        log.warn("Observation sanitaire consignée : {}", messageAlerte);

        // 1. Alerte générale unique sur l'animal
        Alerte alerte = new Alerte();
        alerte.setAnimal(animal);
        alerte.setType(TypeAlerte.ANOMALIE_SANTE);
        alerte.setMessage(messageAlerte);
        alerte.setNiveau(niveau);
        alerte.setStatutAlerte(StatutAlerte.NOUVELLE);
        alerte.setDateHeure(maintenant);
        Alerte alerteEnregistree = alerteRepository.save(alerte);

        // 2. Notification vers l'éleveur propriétaire de la bergerie
        Bergerie bergerie = animal.getBergerie();
        if (bergerie != null && bergerie.getEleveur() != null) {
            Eleveur eleveur = bergerie.getEleveur();
            Notification notifEleveur = new Notification(alerteEnregistree, eleveur, CanalNotif.PUSH);
            notificationRepository.save(notifEleveur);
            log.info("[NOTIFICATION] Alerte transmise à l'éleveur : {}", eleveur.getEmail());
        }

        // 3. Notification vers les vétérinaires au compte validé
        List<Veterinaire> veterinairesValides = veterinaireRepository
                .findByStatutValidationOrderByNomAsc(StatutValidation.VALIDE);

        for (Veterinaire vet : veterinairesValides) {
            Notification notifVet = new Notification(alerteEnregistree, vet, CanalNotif.EMAIL);
            notificationRepository.save(notifVet);
            log.info("[NOTIFICATION] Alerte transmise au vétérinaire : {}", vet.getEmail());
        }

        // 4. Si risque CRITIQUE ou ELEVE, ajuster le statut de santé de l'animal
        if ("CRITIQUE".equalsIgnoreCase(prediction.getNiveauRisque())) {
            animal.setStatutSante(StatutSante.CRITIQUE);
            animalRepository.save(animal);
        } else if ("ELEVE".equalsIgnoreCase(prediction.getNiveauRisque()) && animal.getStatutSante() == StatutSante.SAIN) {
            animal.setStatutSante(StatutSante.EN_OBSERVATION);
            animalRepository.save(animal);
        }
    }

    /**
     * Mappe le niveau de risque texte du modèle vers l'énumération NiveauAlerte du projet.
     */
    private NiveauAlerte mapperNiveauAlerte(String niveauRisque) {
        if (niveauRisque == null) return NiveauAlerte.NORMAL;
        try {
            return NiveauAlerte.valueOf(niveauRisque.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return NiveauAlerte.NORMAL;
        }
    }

    /**
     * Calcule la distance cumulée parcourue en kilomètres entre les points GPS successifs
     * par la formule de Haversine.
     */
    private double calculerDistanceTotaleKm(List<Localisation> positions) {
        if (positions == null || positions.size() < 2) {
            return 0.0;
        }

        double totalKm = 0.0;
        for (int i = 0; i < positions.size() - 1; i++) {
            Localisation p1 = positions.get(i);
            Localisation p2 = positions.get(i + 1);
            if (p1.getLatitude() != null && p1.getLongitude() != null
                    && p2.getLatitude() != null && p2.getLongitude() != null) {
                totalKm += haversine(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
            }
        }
        return totalKm;
    }

    /**
     * Calcule la vitesse moyenne horaire à partir des mesures GPS ou de la distance totale.
     */
    private double calculerVitesseMoyenneKmh(List<Localisation> positions, double distanceTotaleKm) {
        if (positions == null || positions.isEmpty()) {
            return 0.0;
        }

        double moyenneVitessesMesurees = positions.stream()
                .filter(p -> p.getVitesse() != null && p.getVitesse() >= 0.0)
                .mapToDouble(Localisation::getVitesse)
                .average()
                .orElse(-1.0);

        if (moyenneVitessesMesurees >= 0.0) {
            return moyenneVitessesMesurees;
        }

        return distanceTotaleKm;
    }

    /**
     * Formule de Haversine pour le calcul de la distance orthodromique entre 2 coordonnées GPS (km).
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2.0) * Math.sin(dLon / 2.0);

        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return R * c;
    }
}
