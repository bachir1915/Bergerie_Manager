package com.bergerie.config;

import com.bergerie.entity.*;
import com.bergerie.enums.*;
import com.bergerie.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            BergerieRepository bergerieRepo,
            ZoneRepository zoneRepo,
            AnimalRepository animalRepo,
            EleveurRepository eleveurRepo,
            VeterinaireRepository vetRepo,
            CapteurRepository capteurRepo,
            AlerteRepository alerteRepo,
            ActeMedicalRepository acteMedicalRepo,
            LocalisationRepository localisationRepo,
            DonneesEnvRepository donneesEnvRepo,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (bergerieRepo.count() > 0) {
                log.info("Données déjà initialisées. Skipping...");
                return;
            }

            log.info("=== Initialisation des données de démonstration ===");

            // ---- BERGERIE ----
            Bergerie bergerie = new Bergerie();
            bergerie.setNom("Bergerie Al-Baraka");
            bergerie.setAdresse("Route nationale N1, Km 45");
            bergerie.setRegion("Tizi Ouzou");
            bergerie.setSuperficieTotale(250.0);
            bergerie = bergerieRepo.save(bergerie);

            // ---- ZONES ----
            Zone zonePaturage = new Zone();
            zonePaturage.setNom("Pâturage Principal");
            zonePaturage.setTypeZone(TypeZone.PATURAGE);
            zonePaturage.setLatitude(36.7183);
            zonePaturage.setLongitude(4.0479);
            zonePaturage.setSuperficie(150.0);
            zonePaturage.setBergerie(bergerie);
            zonePaturage = zoneRepo.save(zonePaturage);

            Zone zoneQuarantaine = new Zone();
            zoneQuarantaine.setNom("Zone de Quarantaine");
            zoneQuarantaine.setTypeZone(TypeZone.QUARANTAINE);
            zoneQuarantaine.setLatitude(36.7200);
            zoneQuarantaine.setLongitude(4.0510);
            zoneQuarantaine.setSuperficie(20.0);
            zoneQuarantaine.setBergerie(bergerie);
            zoneQuarantaine = zoneRepo.save(zoneQuarantaine);

            Zone zoneAbreuvoir = new Zone();
            zoneAbreuvoir.setNom("Abreuvoir Central");
            zoneAbreuvoir.setTypeZone(TypeZone.ABREUVOIR);
            zoneAbreuvoir.setLatitude(36.7160);
            zoneAbreuvoir.setLongitude(4.0460);
            zoneAbreuvoir.setSuperficie(10.0);
            zoneAbreuvoir.setBergerie(bergerie);
            zoneAbreuvoir = zoneRepo.save(zoneAbreuvoir);

            // ---- ELEVEUR ----
            Eleveur eleveur = new Eleveur();
            eleveur.setNom("Benali");
            eleveur.setPrenom("Mohammed");
            eleveur.setEmail("eleveur@bergerie.dz");
            eleveur.setTelephone("0661234567");
            eleveur.setMotDePasse(passwordEncoder.encode("password123"));
            eleveur.setRole(Role.ELEVEUR);
            eleveur.setStatutCompte(StatutCompte.ACTIF);
            eleveur.setDateCreation(LocalDateTime.now());
            eleveur.setNumEleveur("ELV-001");
            eleveur.setBergerie(bergerie);
            eleveur = eleveurRepo.save(eleveur);

            // ---- VETERINAIRE ----
            Veterinaire vet = new Veterinaire();
            vet.setNom("Hamidi");
            vet.setPrenom("Samira");
            vet.setEmail("vet@bergerie.dz");
            vet.setTelephone("0770987654");
            vet.setMotDePasse(passwordEncoder.encode("password123"));
            vet.setRole(Role.VETERINAIRE);
            vet.setStatutCompte(StatutCompte.ACTIF);
            vet.setDateCreation(LocalDateTime.now());
            vet.setNumeroOrdre("VET-DZ-4521");
            vet.setSpecialite(Specialite.MEDECINE_GENERALE);
            vet = vetRepo.save(vet);

            // ---- CAPTEURS ----
            Capteur capteurDHT1 = new Capteur();
            capteurDHT1.setType(TypeCapteur.DHT22);
            capteurDHT1.setStatut(StatutCapteur.ACTIF);
            capteurDHT1.setDateInstallation(LocalDate.now().minusMonths(6));
            capteurDHT1.setDerniereSynchro(LocalDateTime.now().minusMinutes(15));
            capteurDHT1.setZone(zonePaturage);
            capteurDHT1 = capteurRepo.save(capteurDHT1);

            Capteur capteurDHT2 = new Capteur();
            capteurDHT2.setType(TypeCapteur.DHT22);
            capteurDHT2.setStatut(StatutCapteur.ACTIF);
            capteurDHT2.setDateInstallation(LocalDate.now().minusMonths(3));
            capteurDHT2.setDerniereSynchro(LocalDateTime.now().minusMinutes(10));
            capteurDHT2.setZone(zoneQuarantaine);
            capteurDHT2 = capteurRepo.save(capteurDHT2);

            // ---- ANIMAUX ----
            String[] noms = {"Layla", "Samir", "Nora", "Karim", "Fatima",
                             "Youssef", "Amira", "Tarek", "Zara", "Rayan"};
            String[] races = {"Ouled Djellal", "Hamra", "Berbère",
                              "Ouled Djellal", "Hamra",
                              "Berbère", "Ouled Djellal", "Hamra",
                              "Berbère", "Ouled Djellal"};
            String[] sexes = {"F","M","F","M","F","M","F","M","F","M"};
            StatutSante[] santes = {StatutSante.BON, StatutSante.BON, StatutSante.MALADE,
                                    StatutSante.BON, StatutSante.EN_OBSERVATION,
                                    StatutSante.BON, StatutSante.BON, StatutSante.CRITIQUE,
                                    StatutSante.BON, StatutSante.CONVALESCENT};

            Animal[] animaux = new Animal[10];
            for (int i = 0; i < 10; i++) {
                Animal animal = new Animal();
                animal.setNom(noms[i]);
                animal.setRace(races[i]);
                animal.setSexe(sexes[i]);
                animal.setPoids(40.0 + (i * 3.5));
                animal.setDateNaissance(LocalDate.now().minusYears(2).minusMonths(i));
                animal.setStatut(StatutAnimal.ACTIF);
                animal.setStatutSante(santes[i]);
                animal.setNumeroPuce("PUCE-" + String.format("%04d", i + 1));
                animal.setBergerie(bergerie);
                animaux[i] = animalRepo.save(animal);
            }

            // Capteurs GPS pour 3 animaux
            for (int i = 0; i < 3; i++) {
                Capteur gps = new Capteur();
                gps.setType(TypeCapteur.GPS);
                gps.setStatut(StatutCapteur.ACTIF);
                gps.setDateInstallation(LocalDate.now().minusMonths(2));
                gps.setDerniereSynchro(LocalDateTime.now().minusMinutes(5));
                gps.setAnimal(animaux[i]);
                capteurRepo.save(gps);
            }

            // ---- LOCALISATIONS ----
            double baseLat = 36.7183;
            double baseLon = 4.0479;
            for (int i = 0; i < 3; i++) {
                Localisation loc = new Localisation();
                loc.setAnimal(animaux[i]);
                loc.setLatitude(baseLat + (i * 0.001));
                loc.setLongitude(baseLon + (i * 0.001));
                loc.setDateHeure(LocalDateTime.now().minusMinutes(5));
                loc.setVitesse(0.5 + i * 0.3);
                loc.setPrecisionGps(2.5);
                localisationRepo.save(loc);
            }

            // ---- DONNÉES ENVIRONNEMENTALES ----
            for (int j = 0; j < 5; j++) {
                DonneesEnvironnementales d1 = new DonneesEnvironnementales();
                d1.setZone(zonePaturage);
                d1.setCapteur(capteurDHT1);
                d1.setTemperature(22.0 + j);
                d1.setHumidite(55.0 + j * 2);
                d1.setQualiteAir(80.0 - j);
                d1.setDateHeure(LocalDateTime.now().minusHours(j));
                donneesEnvRepo.save(d1);

                DonneesEnvironnementales d2 = new DonneesEnvironnementales();
                d2.setZone(zoneQuarantaine);
                d2.setCapteur(capteurDHT2);
                d2.setTemperature(25.0 + j);
                d2.setHumidite(60.0 + j);
                d2.setQualiteAir(75.0 - j);
                d2.setDateHeure(LocalDateTime.now().minusHours(j));
                donneesEnvRepo.save(d2);
            }

            // ---- ACTES MÉDICAUX ----
            ActeMedical acte1 = new ActeMedical();
            acte1.setAnimal(animaux[2]); // Nora (malade)
            acte1.setUtilisateur(vet);
            acte1.setTypeSoin(TypeSoin.TRAITEMENT);
            acte1.setDescription("Traitement antibiotique pour infection respiratoire");
            acte1.setMedicament("Amoxicilline");
            acte1.setDose("10mg/kg/j pendant 7 jours");
            acte1.setDateSoin(LocalDate.now().minusDays(3));
            acte1.setProchainRappel(LocalDate.now().plusDays(4));
            acteMedicalRepo.save(acte1);

            ActeMedical acte2 = new ActeMedical();
            acte2.setAnimal(animaux[0]);
            acte2.setUtilisateur(vet);
            acte2.setTypeSoin(TypeSoin.VACCINATION);
            acte2.setDescription("Vaccination annuelle contre la fièvre aphteuse");
            acte2.setMedicament("Vaccin FMD");
            acte2.setDose("2ml IM");
            acte2.setDateSoin(LocalDate.now().minusMonths(1));
            acte2.setProchainRappel(LocalDate.now().plusMonths(11));
            acteMedicalRepo.save(acte2);

            ActeMedical acte3 = new ActeMedical();
            acte3.setAnimal(animaux[7]); // Tarek (critique)
            acte3.setUtilisateur(vet);
            acte3.setTypeSoin(TypeSoin.CONSULTATION);
            acte3.setDescription("Consultation urgente - symptômes de pneumonie");
            acte3.setDateSoin(LocalDate.now());
            acteMedicalRepo.save(acte3);

            // ---- ALERTES ----
            Alerte alerte1 = new Alerte();
            alerte1.setAnimal(animaux[2]);
            alerte1.setUtilisateurDest(eleveur);
            alerte1.setType(TypeAlerte.ANOMALIE_SANTE);
            alerte1.setMessage("Animal 'Nora' présentant des signes de maladie respiratoire");
            alerte1.setNiveau(NiveauAlerte.CRITIQUE);
            alerte1.setStatutAlerte(StatutAlerte.LUE);
            alerte1.setDateHeure(LocalDateTime.now().minusHours(5));
            alerteRepo.save(alerte1);

            Alerte alerte2 = new Alerte();
            alerte2.setAnimal(animaux[7]);
            alerte2.setUtilisateurDest(eleveur);
            alerte2.setType(TypeAlerte.ANOMALIE_SANTE);
            alerte2.setMessage("Animal 'Tarek' en état critique - intervention vétérinaire urgente");
            alerte2.setNiveau(NiveauAlerte.URGENCE);
            alerte2.setStatutAlerte(StatutAlerte.NON_LUE);
            alerte2.setDateHeure(LocalDateTime.now().minusHours(1));
            alerteRepo.save(alerte2);

            Alerte alerte3 = new Alerte();
            alerte3.setAnimal(animaux[7]);
            alerte3.setUtilisateurDest(vet);
            alerte3.setType(TypeAlerte.ANOMALIE_SANTE);
            alerte3.setMessage("Demande de consultation urgente pour 'Tarek'");
            alerte3.setNiveau(NiveauAlerte.URGENCE);
            alerte3.setStatutAlerte(StatutAlerte.NON_LUE);
            alerte3.setDateHeure(LocalDateTime.now().minusMinutes(30));
            alerteRepo.save(alerte3);

            Alerte alerte4 = new Alerte();
            alerte4.setUtilisateurDest(eleveur);
            alerte4.setType(TypeAlerte.TEMPERATURE_CRITIQUE);
            alerte4.setMessage("Température élevée détectée dans la zone Pâturage Principal: 41.5°C");
            alerte4.setNiveau(NiveauAlerte.CRITIQUE);
            alerte4.setStatutAlerte(StatutAlerte.NON_LUE);
            alerte4.setDateHeure(LocalDateTime.now().minusMinutes(15));
            alerteRepo.save(alerte4);

            log.info("=== Données initialisées avec succès ===");
            log.info("Connexion Éleveur : eleveur@bergerie.dz / password123");
            log.info("Connexion Vétérinaire : vet@bergerie.dz / password123");
        };
    }
}
