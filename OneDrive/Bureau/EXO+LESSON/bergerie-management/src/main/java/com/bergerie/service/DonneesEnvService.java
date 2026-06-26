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

@Service
@RequiredArgsConstructor
@Slf4j
public class DonneesEnvService {

    private final DonneesEnvRepository donneesEnvRepository;
    private final ZoneRepository zoneRepository;
    private final CapteurRepository capteurRepository;
    private final AlerteService alerteService;

    // Seuils d'alerte
    private static final double TEMP_MAX = 40.0;
    private static final double TEMP_MIN = 5.0;
    private static final double HUMIDITE_MAX = 90.0;
    private static final double HUMIDITE_MIN = 20.0;

    public List<DonneesEnvironnementales> findAll() {
        return donneesEnvRepository.findAll();
    }

    public List<DonneesEnvironnementales> findRecentes() {
        return donneesEnvRepository.findTop10ByOrderByDateHeureDesc();
    }

    public List<DonneesEnvironnementales> findByZone(Long zoneId) {
        return donneesEnvRepository.findByZoneIdOrderByDateHeureDesc(zoneId);
    }

    @Transactional
    public DonneesEnvironnementales enregistrerDonnees(Long zoneId, Long capteurId,
                                                        Double temperature, Double humidite,
                                                        Double qualiteAir) {
        Zone zone = zoneRepository.findById(zoneId)
            .orElseThrow(() -> new RuntimeException("Zone non trouvée: " + zoneId));
        Capteur capteur = capteurRepository.findById(capteurId)
            .orElseThrow(() -> new RuntimeException("Capteur non trouvé: " + capteurId));

        DonneesEnvironnementales donnees = new DonneesEnvironnementales();
        donnees.setZone(zone);
        donnees.setCapteur(capteur);
        donnees.setTemperature(temperature);
        donnees.setHumidite(humidite);
        donnees.setQualiteAir(qualiteAir);
        donnees.setDateHeure(LocalDateTime.now());

        // Mise à jour dernière synchro du capteur
        capteur.setDerniereSynchro(LocalDateTime.now());
        capteurRepository.save(capteur);

        DonneesEnvironnementales saved = donneesEnvRepository.save(donnees);

        // Vérification des seuils → alertes automatiques
        verifierSeuils(zone, capteur, temperature, humidite);

        return saved;
    }

    private void verifierSeuils(Zone zone, Capteur capteur, Double temp, Double humidite) {
        if (temp > TEMP_MAX) {
            String msg = String.format("Température critique dans la zone '%s': %.1f°C (max: %.1f°C)",
                    zone.getNom(), temp, TEMP_MAX);
            alerteService.creerAlerte(null, capteur, TypeAlerte.TEMPERATURE_CRITIQUE, msg, NiveauAlerte.CRITIQUE);
        } else if (temp < TEMP_MIN) {
            String msg = String.format("Température trop basse dans la zone '%s': %.1f°C (min: %.1f°C)",
                    zone.getNom(), temp, TEMP_MIN);
            alerteService.creerAlerte(null, capteur, TypeAlerte.TEMPERATURE_CRITIQUE, msg, NiveauAlerte.WARNING);
        }

        if (humidite > HUMIDITE_MAX) {
            String msg = String.format("Humidité critique dans la zone '%s': %.1f%% (max: %.1f%%)",
                    zone.getNom(), humidite, HUMIDITE_MAX);
            alerteService.creerAlerte(null, capteur, TypeAlerte.HUMIDITE_CRITIQUE, msg, NiveauAlerte.WARNING);
        } else if (humidite < HUMIDITE_MIN) {
            String msg = String.format("Humidité trop basse dans la zone '%s': %.1f%% (min: %.1f%%)",
                    zone.getNom(), humidite, HUMIDITE_MIN);
            alerteService.creerAlerte(null, capteur, TypeAlerte.HUMIDITE_CRITIQUE, msg, NiveauAlerte.WARNING);
        }
    }
}
