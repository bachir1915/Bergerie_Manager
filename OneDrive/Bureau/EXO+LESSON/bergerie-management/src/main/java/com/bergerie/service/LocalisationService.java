package com.bergerie.service;

import com.bergerie.entity.*;
import com.bergerie.enums.*;
import com.bergerie.repository.*;
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
    private final AlerteService alerteService;

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
    public Localisation enregistrerPosition(Long animalId, Double latitude, Double longitude,
                                            Double vitesse, Double precisionGps) {
        Animal animal = animalRepository.findById(animalId)
            .orElseThrow(() -> new RuntimeException("Animal non trouvé: " + animalId));

        Localisation loc = new Localisation();
        loc.setAnimal(animal);
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        loc.setVitesse(vitesse);
        loc.setPrecisionGps(precisionGps);
        loc.setDateHeure(LocalDateTime.now());

        Localisation saved = localisationRepository.save(loc);

        // Vérification sortie de zone (logique de base — sera améliorée avec Arduino)
        verifierSortieZone(animal, latitude, longitude);

        return saved;
    }

    private void verifierSortieZone(Animal animal, Double latitude, Double longitude) {
        Bergerie bergerie = animal.getBergerie();
        if (bergerie == null) return;

        // Vérification simple : si aucune zone de la bergerie ne contient l'animal
        List<Zone> zones = bergerie.getZones();
        boolean dansUneZone = false;

        for (Zone zone : zones) {
            if (zone.getLatitude() != null && zone.getLongitude() != null && zone.getSuperficie() != null) {
                double distance = calculerDistance(latitude, longitude,
                                                   zone.getLatitude(), zone.getLongitude());
                double rayon = Math.sqrt(zone.getSuperficie() / Math.PI) * 0.001; // approximation km
                if (distance < rayon) {
                    dansUneZone = true;
                    break;
                }
            }
        }

        if (!zones.isEmpty() && !dansUneZone) {
            String msg = String.format("L'animal '%s' (ID: %d) a quitté sa zone autorisée. Position: [%.4f, %.4f]",
                    animal.getNom(), animal.getId(), latitude, longitude);
            alerteService.creerAlerte(animal, null, TypeAlerte.SORTIE_ZONE, msg, NiveauAlerte.URGENCE);
            log.warn("[SORTIE ZONE] {}", msg);
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
