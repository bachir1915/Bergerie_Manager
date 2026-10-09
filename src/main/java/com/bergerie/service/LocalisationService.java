package com.bergerie.service;

import com.bergerie.entity.Animal;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.Capteur;
import com.bergerie.entity.Localisation;
import com.bergerie.entity.Zone;
import com.bergerie.enums.NiveauAlerte;
import com.bergerie.enums.TypeAlerte;
import com.bergerie.repository.AnimalRepository;
import com.bergerie.repository.CapteurRepository;
import com.bergerie.repository.LocalisationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalisationService {

    private final LocalisationRepository localisationRepository;
    private final AnimalRepository animalRepository;
    private final CapteurRepository capteurRepository;
    private final AlerteService alerteService;
    private final com.bergerie.repository.AlerteRepository alerteRepository;
    private final com.bergerie.repository.ZoneRepository zoneRepository;

    public List<Localisation> findByAnimal(Long animalId) {
        return localisationRepository.findByAnimalIdOrderByDateHeureDesc(animalId);
    }

    public List<Localisation> findDernierePositionTousAnimaux() {
        return localisationRepository.findLatestForAllAnimals();
    }

    public Optional<Localisation> findDernierePosition(Long animalId) {
        return localisationRepository.findLatestByAnimalId(animalId);
    }

    @Transactional
    public Localisation enregistrerPosition(Long animalId, Long capteurId, Double latitude, Double longitude,
                                            Double vitesse, Double precisionGps) {
        Animal animal = animalRepository.findById(animalId)
            .orElseThrow(() -> new RuntimeException("Animal non trouvé: " + animalId));

        Localisation loc = new Localisation();
        loc.setAnimal(animal);
        if (capteurId != null) {
            Capteur capteur = capteurRepository.findById(capteurId)
                .orElseThrow(() -> new RuntimeException("Capteur non trouvé: " + capteurId));
            loc.setCapteur(capteur);
        }
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        loc.setVitesse(vitesse);
        loc.setPrecisionGps(precisionGps);
        loc.setDateHeure(LocalDateTime.now());

        Localisation saved = localisationRepository.save(loc);

        // Vérification sortie de zone (déclenche une alerte géofencing si l'animal franchit le périmètre)
        verifierSortieZone(animal, latitude, longitude);

        return saved;
    }

    private void verifierSortieZone(Animal animal, Double latitude, Double longitude) {
        Bergerie bergerie = animal.getBergerie();
        if (bergerie == null) return;

        List<Zone> zones = zoneRepository.findByBergerieId(bergerie.getId());
        boolean dansUneZone = false;

        for (Zone zone : zones) {
            if (zone.getLatitude() != null && zone.getLongitude() != null && zone.getSuperficie() != null) {
                double distance = calculerDistance(latitude, longitude,
                                                   zone.getLatitude(), zone.getLongitude());
                // Rayon de la zone avec seuil de tolérance minimal de 120m (0.12 km)
                double rayon = Math.max(0.120, Math.sqrt(zone.getSuperficie() / Math.PI) * 0.001);
                if (distance <= rayon) {
                    dansUneZone = true;
                    break;
                }
            }
        }

        if (!zones.isEmpty() && !dansUneZone) {
            // Anti-spam : vérifier si une alerte SORTIE_ZONE existe déjà pour cet animal dans les 5 dernières minutes
            boolean alerteRecente = alerteRepository.findByAnimalIdOrderByDateHeureDesc(animal.getId())
                    .stream()
                    .filter(a -> a.getType() == TypeAlerte.SORTIE_ZONE)
                    .anyMatch(a -> a.getDateHeure().isAfter(LocalDateTime.now().minusMinutes(5)));

            if (!alerteRecente) {
                String msg = String.format("Alerte Géofencing : L'animal '%s' (ID: %d) a quitté sa zone pastorale ! Position actuelle: [%.5f, %.5f]",
                        animal.getNom(), animal.getId(), latitude, longitude);
                alerteService.creerAlerte(animal, TypeAlerte.SORTIE_ZONE, msg, NiveauAlerte.CRITIQUE);
                log.warn("[SORTIE ZONE] {}", msg);
            }
        }
    }

    /**
     * Calcule la distance en km entre deux points GPS (formule Haversine)
     */
    private double calculerDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
