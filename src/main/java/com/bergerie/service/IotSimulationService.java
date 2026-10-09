package com.bergerie.service;

import com.bergerie.entity.Animal;
import com.bergerie.entity.Capteur;
import com.bergerie.entity.CapteurAnimal;
import com.bergerie.entity.CapteurZone;
import com.bergerie.entity.Zone;
import com.bergerie.enums.StatutAnimal;
import com.bergerie.enums.StatutCapteur;
import com.bergerie.enums.TypeCapteur;
import com.bergerie.repository.AnimalRepository;
import com.bergerie.repository.CapteurAnimalRepository;
import com.bergerie.repository.CapteurRepository;
import com.bergerie.repository.CapteurZoneRepository;
import com.bergerie.repository.ZoneRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service de simulation IoT en temps réel pour la Bergerie.
 * Génère automatiquement :
 *  1. Les déplacements GPS réalistes des animaux équipés de colliers (ESP32 + NEO-6M).
 *  2. Les relevés de température, humidité et qualité de l'air (ESP32 + DHT22 + MQ-135).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IotSimulationService {

    private final AnimalRepository animalRepository;
    private final ZoneRepository zoneRepository;
    private final CapteurRepository capteurRepository;
    private final CapteurAnimalRepository capteurAnimalRepository;
    private final CapteurZoneRepository capteurZoneRepository;
    private final LocalisationService localisationService;
    private final DonneesEnvService donneesEnvService;
    private final com.bergerie.repository.LocalisationRepository localisationRepository;

    @Value("${simulation.iot.enabled:true}")
    private boolean simulationEnabledConfig;

    private final AtomicBoolean active = new AtomicBoolean(true);
    private final AtomicLong gpsTickCount = new AtomicLong(0);
    private final AtomicLong envTickCount = new AtomicLong(0);
    private LocalDateTime dernierGpsTick;
    private LocalDateTime dernierEnvTick;

    private final Random random = new Random();

    // Mémoire de la dernière position connue de chaque animal pour un mouvement continu et fluide
    private final Map<Long, PositionSimulee> positionsAnimaux = new ConcurrentHashMap<>();

    // Mémoire des valeurs climatiques ambiantes par zone
    private final Map<Long, ClimatZoneSimule> climatsZones = new ConcurrentHashMap<>();

    @Data
    public static class PositionSimulee {
        private double latitude;
        private double longitude;

        public PositionSimulee(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Data
    public static class ClimatZoneSimule {
        private double temperature;
        private double humidite;
        private double qualiteAir;

        public ClimatZoneSimule(double temperature, double humidite, double qualiteAir) {
            this.temperature = temperature;
            this.humidite = humidite;
            this.qualiteAir = qualiteAir;
        }
    }

    /**
     * Tâche planifiée pour simuler les coordonnées GPS des animaux.
     * Exécutée toutes les 15 secondes par défaut (configurable via simulation.iot.gps-delay).
     */
    @Scheduled(fixedDelayString = "${simulation.iot.gps-delay:15000}", initialDelay = 5000)
    public void scheduleSimulationGps() {
        if (!isSimulationActive()) {
            return;
        }
        simulerTickGps();
    }

    /**
     * Tâche planifiée pour simuler les capteurs d'ambiance (DHT22 & MQ-135).
     * Exécutée toutes les 30 secondes par défaut (configurable via simulation.iot.env-delay).
     */
    @Scheduled(fixedDelayString = "${simulation.iot.env-delay:30000}", initialDelay = 10000)
    public void scheduleSimulationEnvironnement() {
        if (!isSimulationActive()) {
            return;
        }
        simulerTickEnv();
    }

    /**
     * Exécute un cycle de simulation GPS pour tous les animaux actifs.
     */
    @Transactional
    public synchronized int simulerTickGps() {
        List<Animal> animaux = animalRepository.findByStatut(StatutAnimal.ACTIF);
        if (animaux.isEmpty()) {
            animaux = animalRepository.findAll();
        }

        int count = 0;
        for (Animal animal : animaux) {
            try {
                simulerPositionPourAnimal(animal);
                count++;
            } catch (Exception e) {
                log.warn("[SIMULATION IoT] Erreur lors de la simulation GPS pour l'animal #{}: {}",
                        animal.getId(), e.getMessage());
            }
        }

        gpsTickCount.incrementAndGet();
        dernierGpsTick = LocalDateTime.now();
        log.info("[SIMULATION IoT] Cycle GPS terminé : {} animaux mis à jour.", count);
        return count;
    }

    /**
     * Exécute un cycle de simulation environnementale pour toutes les zones.
     */
    @Transactional
    public synchronized int simulerTickEnv() {
        List<Zone> zones = zoneRepository.findAll();
        int count = 0;

        for (Zone zone : zones) {
            try {
                simulerClimatPourZone(zone);
                count++;
            } catch (Exception e) {
                log.warn("[SIMULATION IoT] Erreur lors de la simulation climatique pour la zone #{}: {}",
                        zone.getId(), e.getMessage());
            }
        }

        envTickCount.incrementAndGet();
        dernierEnvTick = LocalDateTime.now();
        log.info("[SIMULATION IoT] Cycle Environnemental terminé : {} zones mesurées.", count);
        return count;
    }

    private void simulerPositionPourAnimal(Animal animal) {
        PositionSimulee pos = positionsAnimaux.computeIfAbsent(animal.getId(), id -> {
            // Récupérer d'abord la dernière position réelle enregistrée en base si elle existe
            var derniereLoc = localisationRepository.findLatestByAnimalId(animal.getId());
            if (derniereLoc.isPresent() && derniereLoc.get().getLatitude() != null && derniereLoc.get().getLongitude() != null) {
                return new PositionSimulee(derniereLoc.get().getLatitude(), derniereLoc.get().getLongitude());
            }

            // Point d'ancrage initial : coordonnées de la zone de la bergerie ou Dakar Parcelles Assainies par défaut
            double baseLat = 14.7585;
            double baseLon = -17.4418;

            if (animal.getBergerie() != null) {
                List<Zone> bZones = zoneRepository.findByBergerieId(animal.getBergerie().getId());
                if (!bZones.isEmpty()) {
                    Zone z = bZones.get(0);
                    if (z.getLatitude() != null && z.getLongitude() != null) {
                        baseLat = z.getLatitude();
                        baseLon = z.getLongitude();
                    }
                }
            }

            // Décalage initial unique par animal dans un rayon de ~40m
            double initLat = baseLat + (random.nextDouble() - 0.5) * 0.0005;
            double initLon = baseLon + (random.nextDouble() - 0.5) * 0.0005;
            return new PositionSimulee(initLat, initLon);
        });

        // Déplacement brownien réaliste avec cohésion pastorale (maintien du troupeau dans son pâturage)
        double centreLat = 14.7585;
        double centreLon = -17.4418;
        if (animal.getBergerie() != null) {
            List<Zone> bZones = zoneRepository.findByBergerieId(animal.getBergerie().getId());
            if (!bZones.isEmpty()) {
                Zone z = bZones.get(0);
                if (z.getLatitude() != null && z.getLongitude() != null) {
                    centreLat = z.getLatitude();
                    centreLon = z.getLongitude();
                }
            }
        }

        // Si l'animal s'éloigne trop du centre de pâturage (> 120m ~ 0.0011 deg), rappel élastique doux vers le groupe
        double ecartLat = pos.getLatitude() - centreLat;
        double ecartLon = pos.getLongitude() - centreLon;
        double rappelLat = Math.abs(ecartLat) > 0.0010 ? -0.25 * ecartLat : 0;
        double rappelLon = Math.abs(ecartLon) > 0.0010 ? -0.25 * ecartLon : 0;

        double deltaLat = (random.nextGaussian() * 0.00006) + rappelLat; // ~6-8m de déplacement
        double deltaLon = (random.nextGaussian() * 0.00006) + rappelLon;

        pos.setLatitude(pos.getLatitude() + deltaLat);
        pos.setLongitude(pos.getLongitude() + deltaLon);

        double vitesse = Math.round((0.5 + random.nextDouble() * 2.0) * 10.0) / 10.0; // 0.5 à 2.5 km/h
        double precision = Math.round((1.4 + random.nextDouble() * 2.0) * 10.0) / 10.0; // 1.4m à 3.4m

        // Récupérer ou créer automatiquement le capteur GPS du collier de cet animal
        Capteur capteurGps = getOrCreateCapteurGps(animal);

        // Enregistrement via le service métier (gère la persistance et les alertes de sortie de zone)
        localisationService.enregistrerPosition(
                animal.getId(),
                capteurGps.getId(),
                pos.getLatitude(),
                pos.getLongitude(),
                vitesse,
                precision
        );
    }

    /**
     * Simule la fugue / sortie de périmètre d'un animal (pour tester le géofencing et les alertes en direct).
     */
    @Transactional
    public Map<String, Object> simulerSortieZone(Long animalId) {
        Animal animal = null;
        if (animalId != null) {
            animal = animalRepository.findById(animalId).orElse(null);
        }
        if (animal == null) {
            List<Animal> actifs = animalRepository.findByStatut(StatutAnimal.ACTIF);
            if (actifs.isEmpty()) actifs = animalRepository.findAll();
            if (actifs.isEmpty()) {
                return Map.of("status", "ERROR", "message", "Aucun animal disponible pour la simulation de sortie");
            }
            animal = actifs.get(random.nextInt(actifs.size()));
        }

        // Trouver le centre de la zone de rattachement
        double baseLat = 14.7585;
        double baseLon = -17.4418;
        if (animal.getBergerie() != null) {
            List<Zone> bZones = zoneRepository.findByBergerieId(animal.getBergerie().getId());
            if (!bZones.isEmpty()) {
                Zone z = bZones.get(0);
                if (z.getLatitude() != null && z.getLongitude() != null) {
                    baseLat = z.getLatitude();
                    baseLon = z.getLongitude();
                }
            }
        }

        // Déplacer l'animal délibérément à ~450-600m hors de la zone autorisée (0.004 deg)
        double angle = random.nextDouble() * 2 * Math.PI;
        double distDeg = 0.0042 + random.nextDouble() * 0.0018; // ~450m à 650m
        double nouvelleLat = baseLat + Math.cos(angle) * distDeg;
        double nouvelleLon = baseLon + Math.sin(angle) * distDeg;

        PositionSimulee pos = positionsAnimaux.computeIfAbsent(animal.getId(), id -> new PositionSimulee(0.0, 0.0));
        pos.setLatitude(nouvelleLat);
        pos.setLongitude(nouvelleLon);

        Capteur capteurGps = getOrCreateCapteurGps(animal);
        double vitesseCourse = 6.2 + random.nextDouble() * 2.5; // Course de fuite ~6-8 km/h

        com.bergerie.entity.Localisation loc = localisationService.enregistrerPosition(
                animal.getId(),
                capteurGps.getId(),
                nouvelleLat,
                nouvelleLon,
                vitesseCourse,
                1.5
        );

        log.warn("[SIMULATION IoT] Sortie de zone simulée pour l'animal '{}' (ID: {}) à [{}, {}]",
                animal.getNom(), animal.getId(), nouvelleLat, nouvelleLon);

        Map<String, Object> res = new java.util.HashMap<>();
        res.put("status", "OK");
        res.put("message", "Alerte de sortie de zone déclenchée avec succès pour l'animal " + animal.getNom());
        res.put("animalId", animal.getId());
        res.put("animalNom", animal.getNom());
        res.put("latitude", nouvelleLat);
        res.put("longitude", nouvelleLon);
        res.put("vitesse", vitesseCourse);
        res.put("localisationId", loc.getId());
        return res;
    }

    /**
     * Ramène tous les animaux (ou un animal spécifique) au centre de leur zone pastorale sécurisée.
     */
    @Transactional
    public Map<String, Object> simulerRetourAuBercail(Long animalId) {
        List<Animal> cibles;
        if (animalId != null) {
            cibles = animalRepository.findById(animalId).map(List::of).orElse(List.of());
        } else {
            cibles = animalRepository.findByStatut(StatutAnimal.ACTIF);
            if (cibles.isEmpty()) cibles = animalRepository.findAll();
        }

        int count = 0;
        for (Animal animal : cibles) {
            double baseLat = 14.7585;
            double baseLon = -17.4418;
            if (animal.getBergerie() != null) {
                List<Zone> bZones = zoneRepository.findByBergerieId(animal.getBergerie().getId());
                if (!bZones.isEmpty()) {
                    Zone z = bZones.get(0);
                    if (z.getLatitude() != null && z.getLongitude() != null) {
                        baseLat = z.getLatitude();
                        baseLon = z.getLongitude();
                    }
                }
            }

            // Retour au bercail avec petit décalage naturel de 15m
            double retourLat = baseLat + (random.nextDouble() - 0.5) * 0.0003;
            double retourLon = baseLon + (random.nextDouble() - 0.5) * 0.0003;

            PositionSimulee pos = positionsAnimaux.computeIfAbsent(animal.getId(), id -> new PositionSimulee(0.0, 0.0));
            pos.setLatitude(retourLat);
            pos.setLongitude(retourLon);

            Capteur capteurGps = getOrCreateCapteurGps(animal);
            localisationService.enregistrerPosition(
                    animal.getId(),
                    capteurGps.getId(),
                    retourLat,
                    retourLon,
                    0.6,
                    1.8
            );
            count++;
        }

        log.info("[SIMULATION IoT] {} animal(aux) ramené(s) au bercail avec succès.", count);
        Map<String, Object> res = new java.util.HashMap<>();
        res.put("status", "OK");
        res.put("message", count + " animal(aux) ramené(s) au bercail avec succès");
        res.put("count", count);
        return res;
    }

    private void simulerClimatPourZone(Zone zone) {
        ClimatZoneSimule climat = climatsZones.computeIfAbsent(zone.getId(), id -> {
            // Valeurs typiques semi-arides (Louga / Sénégal) : ~32°C, 42% humidité, air pur à moyen
            return new ClimatZoneSimule(
                    31.0 + random.nextDouble() * 3.0,
                    40.0 + random.nextDouble() * 10.0,
                    60.0 + random.nextDouble() * 20.0
            );
        });

        // Légères variations naturelles
        double deltaT = (random.nextGaussian()) * 0.25;
        double deltaH = (random.nextGaussian()) * 0.5;
        double deltaAir = (random.nextGaussian()) * 1.5;

        climat.setTemperature(Math.max(18.0, Math.min(44.0, climat.getTemperature() + deltaT)));
        climat.setHumidite(Math.max(20.0, Math.min(85.0, climat.getHumidite() + deltaH)));
        climat.setQualiteAir(Math.max(30.0, Math.min(180.0, climat.getQualiteAir() + deltaAir)));

        double tempArrondie = Math.round(climat.getTemperature() * 10.0) / 10.0;
        double humArrondie = Math.round(climat.getHumidite() * 10.0) / 10.0;
        double airArrondi = Math.round(climat.getQualiteAir() * 10.0) / 10.0;

        Capteur capteurDht = getOrCreateCapteurDht(zone);

        // Enregistrement via le service métier (déclenche automatiquement les alertes si seuil franchi)
        donneesEnvService.enregistrerDonnees(
                zone.getId(),
                capteurDht.getId(),
                tempArrondie,
                humArrondie,
                airArrondi
        );
    }

    private Capteur getOrCreateCapteurGps(Animal animal) {
        return capteurAnimalRepository.findByAnimalIdAndType(animal.getId(), TypeCapteur.GPS)
                .map(ca -> (Capteur) ca)
                .orElseGet(() -> {
                    CapteurAnimal capteur = new CapteurAnimal();
                    capteur.setType(TypeCapteur.GPS);
                    capteur.setStatut(StatutCapteur.ACTIF);
                    capteur.setDateInstallation(LocalDate.now());
                    capteur.setAnimal(animal);
                    CapteurAnimal saved = capteurAnimalRepository.save(capteur);
                    log.info("[SIMULATION IoT] Nouveau capteur GPS auto-créé (ID: {}) pour l'animal '{}' (ID: {})",
                            saved.getId(), animal.getNom(), animal.getId());
                    return saved;
                });
    }

    private Capteur getOrCreateCapteurDht(Zone zone) {
        List<CapteurZone> dhtList = capteurZoneRepository.findByZoneId(zone.getId());
        if (!dhtList.isEmpty()) {
            return dhtList.get(0);
        }

        CapteurZone capteur = new CapteurZone();
        capteur.setType(TypeCapteur.TEMPERATURE);
        capteur.setStatut(StatutCapteur.ACTIF);
        capteur.setDateInstallation(LocalDate.now());
        capteur.setZone(zone);
        CapteurZone saved = capteurZoneRepository.save(capteur);
        log.info("[SIMULATION IoT] Nouveau capteur d'ambiance auto-créé (ID: {}) pour la zone '{}'", saved.getId(), zone.getNom());
        return saved;
    }

    // ---- Méthodes de contrôle ----

    public boolean isSimulationActive() {
        return simulationEnabledConfig && active.get();
    }

    public boolean toggleSimulation() {
        boolean nouvelEtat = !active.get();
        active.set(nouvelEtat);
        log.info("[SIMULATION IoT] État de la simulation changé -> actif = {}", nouvelEtat);
        return nouvelEtat;
    }

    public void setSimulationActive(boolean state) {
        active.set(state);
    }

    public long getGpsTickCount() {
        return gpsTickCount.get();
    }

    public long getEnvTickCount() {
        return envTickCount.get();
    }

    public LocalDateTime getDernierGpsTick() {
        return dernierGpsTick;
    }

    public LocalDateTime getDernierEnvTick() {
        return dernierEnvTick;
    }
}
