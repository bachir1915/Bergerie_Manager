package com.bergerie.config;

import com.bergerie.entity.ActeMedical;
import com.bergerie.entity.Admin;
import com.bergerie.entity.Alerte;
import com.bergerie.entity.Animal;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.CapteurAnimal;
import com.bergerie.entity.CapteurZone;
import com.bergerie.entity.DonneesEnvironnementales;
import com.bergerie.entity.Eleveur;
import com.bergerie.entity.Localisation;
import com.bergerie.entity.Notification;
import com.bergerie.entity.Veterinaire;
import com.bergerie.entity.Zone;
import com.bergerie.enums.CanalNotif;
import com.bergerie.enums.NiveauAlerte;
import com.bergerie.enums.StatutAlerte;
import com.bergerie.enums.StatutCapteur;
import com.bergerie.enums.StatutSante;
import com.bergerie.enums.StatutValidation;
import com.bergerie.enums.TypeAlerte;
import com.bergerie.enums.TypeCapteur;
import com.bergerie.enums.TypeSoin;
import com.bergerie.enums.TypeZone;
import com.bergerie.repository.ActeMedicalRepository;
import com.bergerie.repository.AdminRepository;
import com.bergerie.repository.AlerteRepository;
import com.bergerie.repository.AnimalRepository;
import com.bergerie.repository.BergerieRepository;
import com.bergerie.repository.CapteurAnimalRepository;
import com.bergerie.repository.CapteurRepository;
import com.bergerie.repository.CapteurZoneRepository;
import com.bergerie.repository.DonneesEnvRepository;
import com.bergerie.repository.EleveurRepository;
import com.bergerie.repository.LocalisationRepository;
import com.bergerie.repository.NotificationRepository;
import com.bergerie.repository.VeterinaireRepository;
import com.bergerie.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Donnees de demonstration -- contexte senegalais.
 * Regions : Louga, Matam, Kaffrine.
 * Races locales : Touabire, Peul-Peul, Ladoum, Warale, Djallonke.
 * Noms d'animaux : wolof / pulaar courants dans l'elevage pastoral.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final EleveurRepository eleveurRepository;
    private final VeterinaireRepository veterinaireRepository;
    private final BergerieRepository bergerieRepository;
    private final AnimalRepository animalRepository;
    private final ZoneRepository zoneRepository;
    private final CapteurRepository capteurRepository;
    private final CapteurAnimalRepository capteurAnimalRepository;
    private final CapteurZoneRepository capteurZoneRepository;
    private final DonneesEnvRepository donneesEnvRepository;
    private final LocalisationRepository localisationRepository;
    private final AlerteRepository alerteRepository;
    private final NotificationRepository notificationRepository;
    private final ActeMedicalRepository acteMedicalRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String pwd = passwordEncoder.encode("password123");

        // ------------------------------------------------------------------ Admin
        Admin admin = new Admin(null, "Admin", "Systeme",
                "admin@bergerie.com", "77 000 00 00", pwd);
        admin.setCodeConnexion("ADM-DEMO-0001");
        adminRepository.save(admin);

        // ------------------------------------------------------------------ Eleveur 1 -- Louga
        Eleveur eleveur1 = new Eleveur(null, "Diallo", "Mamadou",
                "eleveur@bergerie.com", "77 345 67 89", pwd, "ELV-001");
        eleveur1.setCodeConnexion("ELV-100001");
        eleveur1 = eleveurRepository.save(eleveur1);

        // ------------------------------------------------------------------ Eleveur 2 -- Matam
        Eleveur eleveur2 = new Eleveur(null, "Ba", "Ibrahima",
                "eleveur2@bergerie.com", "77 456 78 90", pwd, "ELV-002");
        eleveur2.setCodeConnexion("ELV-100002");
        eleveur2 = eleveurRepository.save(eleveur2);

        // ------------------------------------------------------------------ Eleveur 3 -- Kaffrine
        Eleveur eleveur3 = new Eleveur(null, "Sow", "Oumar",
                "eleveur3@bergerie.com", "77 567 89 01", pwd, "ELV-003");
        eleveur3.setCodeConnexion("ELV-100003");
        eleveur3 = eleveurRepository.save(eleveur3);

        // ------------------------------------------------------------------ Veterinaire valide (compte de demo)
        Veterinaire vetValide = new Veterinaire(
                null, "Ndiaye", "Fatou", "vet@bergerie.com", "77 600 11 22", pwd);
        vetValide.setStatutValidation(StatutValidation.VALIDE);
        vetValide.setSpecialite("Ruminants & Camelides");
        veterinaireRepository.save(vetValide);

        // ------------------------------------------------------------------ Veterinaire valide 2
        Veterinaire vetValide2 = new Veterinaire(
                null, "Diop", "Moussa", "diop.moussa.vet@gmail.com", "77 812 33 44", pwd);
        vetValide2.setStatutValidation(StatutValidation.VALIDE);
        vetValide2.setSpecialite("Medecine interne & Parasitologie");
        veterinaireRepository.save(vetValide2);

        // ------------------------------------------------------------------ Veterinaire valide 3
        Veterinaire vetValide3 = new Veterinaire(
                null, "Sarr", "Aminata", "aminata.sarr.vet@outlook.com", "78 934 55 66", pwd);
        vetValide3.setStatutValidation(StatutValidation.VALIDE);
        vetValide3.setSpecialite("Reproduction & Obstetrique");
        veterinaireRepository.save(vetValide3);

        // ------------------------------------------------------------------ Veterinaire en attente
        Veterinaire vetEnAttente = new Veterinaire(
                null, "Fall", "Abdou", "vet-attente@bergerie.com", "77 700 22 33", pwd);
        vetEnAttente.setStatutValidation(StatutValidation.EN_ATTENTE);
        vetEnAttente.setSpecialite("Chirurgie & Obstetrique");
        veterinaireRepository.save(vetEnAttente);

        // ================================================================== Bergerie 1 -- Louga (compte de demo)
        Bergerie bergerie1 = new Bergerie();
        bergerie1.setNom("Bergerie Al-Baraka");
        bergerie1.setCode("AB-12-IV-34");   // code utilise par le veterinaire de demo
        bergerie1.setAdresse("Parcelles Assainies, Dakar");
        bergerie1.setRegion("Dakar");
        bergerie1.setSuperficieTotale(1500.0);
        bergerie1.setEleveur(eleveur1);
        bergerie1 = bergerieRepository.save(bergerie1);

        // Zones pastorales -- Parcelles Assainies, Dakar, Sénégal (autour de 14.7585N, -17.4420W)
        Zone zoneNord = makeZone("Enclos Parcelles Assainies", 14.7585, -17.4420, 8000.0, bergerie1, TypeZone.PATURAGE);
        Zone zoneSud  = makeZone("Paturage Unite 15",          14.7570, -17.4405, 6000.0, bergerie1, TypeZone.PATURAGE);
        Zone zoneAbri = makeZone("Bercail Principal",          14.7580, -17.4415, 2500.0, bergerie1, TypeZone.ABRI);
        zoneNord = zoneRepository.save(zoneNord);
        zoneSud  = zoneRepository.save(zoneSud);
        zoneAbri = zoneRepository.save(zoneAbri);

        // Animaux -- noms wolof/pulaar typiques de l'elevage senegalais
        Animal a1 = animal("Thiouppe", "Touabire",  "F", 55.0, 3, StatutSante.SAIN,           "SN-TBR-0001", bergerie1);
        Animal a2 = animal("Ndeye",    "Touabire",  "F", 52.0, 2, StatutSante.SAIN,           "SN-TBR-0002", bergerie1);
        Animal a3 = animal("Samba",    "Ladoum",    "M", 80.0, 4, StatutSante.SAIN,           "SN-LDM-0001", bergerie1);
        Animal a4 = animal("Aissa",    "Peul-Peul", "F", 40.0, 1, StatutSante.EN_OBSERVATION, "SN-PPL-0001", bergerie1);
        Animal a5 = animal("Matar",    "Peul-Peul", "M", 38.0, 1, StatutSante.SAIN,           "SN-PPL-0002", bergerie1);
        Animal a6 = animal("Binta",    "Warale",    "F", 35.0, 0, StatutSante.CRITIQUE,       "SN-WRL-0001", bergerie1);
        Animal a7 = animal("Diouf",    "Touabire",  "M", 65.0, 5, StatutSante.SAIN,           "SN-TBR-0003", bergerie1);
        Animal a8 = animal("Khadija",  "Ladoum",    "F", 70.0, 3, StatutSante.SAIN,           "SN-LDM-0002", bergerie1);
        a1 = animalRepository.save(a1); a2 = animalRepository.save(a2);
        a3 = animalRepository.save(a3); a4 = animalRepository.save(a4);
        a5 = animalRepository.save(a5); a6 = animalRepository.save(a6);
        a7 = animalRepository.save(a7); a8 = animalRepository.save(a8);

        // Capteur IoT zone nord (CapteurZone)
        CapteurZone capteur1 = new CapteurZone();
        capteur1.setType(TypeCapteur.TEMPERATURE);
        capteur1.setStatut(StatutCapteur.ACTIF);
        capteur1.setDateInstallation(LocalDate.now().minusMonths(4));
        capteur1.setZone(zoneNord);
        capteur1 = capteurZoneRepository.save(capteur1);

        DonneesEnvironnementales mesure1 = new DonneesEnvironnementales();
        mesure1.setZone(zoneNord);
        mesure1.setCapteur(capteur1);
        mesure1.setTemperature(27.5);   // climat littoral Dakar
        mesure1.setHumidite(65.0);
        mesure1.setQualiteAir(70.0);
        mesure1.setDateHeure(LocalDateTime.now());
        donneesEnvRepository.save(mesure1);

        // Capteurs IoT GPS pour animaux (Parcelles Assainies, Dakar)
        initPositionGps(a1, 14.7585, -17.4418, 1.2);
        initPositionGps(a2, 14.7582, -17.4422, 0.8);
        initPositionGps(a3, 14.7588, -17.4415, 1.4);
        initPositionGps(a4, 14.7578, -17.4425, 0.6);
        initPositionGps(a5, 14.7584, -17.4410, 1.0);
        initPositionGps(a6, 14.7575, -17.4418, 0.5);
        initPositionGps(a7, 14.7589, -17.4423, 1.3);
        initPositionGps(a8, 14.7580, -17.4412, 0.9);

        // Alertes
        Alerte al1 = new Alerte();
        al1.setAnimal(a6);
        al1.setType(TypeAlerte.ANOMALIE_SANTE);
        al1.setMessage("Binta presente une forte fievre (41 degres C) et un refus d'alimentation depuis hier.");
        al1.setNiveau(NiveauAlerte.CRITIQUE);
        al1.setStatutAlerte(StatutAlerte.NOUVELLE);
        al1.setDateHeure(LocalDateTime.now());
        al1 = alerteRepository.save(al1);

        // Notifications associées à l'alerte 1
        notificationRepository.save(new Notification(al1, eleveur1, CanalNotif.PUSH));
        notificationRepository.save(new Notification(al1, vetValide, CanalNotif.EMAIL));

        Alerte al2 = new Alerte();
        al2.setAnimal(a4);
        al2.setType(TypeAlerte.ANOMALIE_SANTE);
        al2.setMessage("Aissa : diarrhee legere depuis 48h, surveillance recommandee.");
        al2.setNiveau(NiveauAlerte.MODERE);
        al2.setStatutAlerte(StatutAlerte.NOUVELLE);
        al2.setDateHeure(LocalDateTime.now().minusHours(2));
        al2 = alerteRepository.save(al2);

        // Notifications associées à l'alerte 2
        notificationRepository.save(new Notification(al2, eleveur1, CanalNotif.PUSH));
        notificationRepository.save(new Notification(al2, vetValide, CanalNotif.EMAIL));

        // Actes medicaux
        acteMedicalRepository.save(acte(a1, vetValide, TypeSoin.VACCINATION,
                "Vaccination contre la pasteurellose",
                "Pastovaccin", "2 mL SC",
                LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(9)));
        acteMedicalRepository.save(acte(a4, vetValide, TypeSoin.CONSULTATION,
                "Examen clinique : diarrhee legere, hydratation correcte",
                "Metronidazole", "10 mg/kg -- 5 jours",
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(3)));
        acteMedicalRepository.save(acte(a6, vetValide, TypeSoin.TRAITEMENT,
                "Antipyretique + perfusion de rehydratation, etat grave",
                "Flunixine + Ringer Lactate", "Selon protocole veterinaire",
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(7)));
        acteMedicalRepository.save(acte(a3, vetValide, TypeSoin.ANALYSE,
                "Bilan parasitaire annuel -- resultats normaux",
                null, null,
                LocalDate.now().minusMonths(1), null));
        acteMedicalRepository.save(acte(a8, vetValide, TypeSoin.VACCINATION,
                "Vaccination PPR (Peste des Petits Ruminants)",
                "Vaccin PPR homologue SENASA", "1 mL SC",
                LocalDate.now().minusDays(10), LocalDate.now().plusYears(1)));

        // ================================================================== Bergerie 2 -- Matam
        Bergerie bergerie2 = new Bergerie();
        bergerie2.setNom("Ranch Fouta Toro");
        bergerie2.setCode("FT-55-X-12");
        bergerie2.setAdresse("Ourossogui, Matam");
        bergerie2.setRegion("Matam");
        bergerie2.setSuperficieTotale(2200.0);
        bergerie2.setEleveur(eleveur2);
        bergerie2 = bergerieRepository.save(bergerie2);

        zoneRepository.save(makeZone("Paturage Thioly",   15.616, -13.317, 900.0, bergerie2, TypeZone.PATURAGE));
        zoneRepository.save(makeZone("Espace Medical FT", 15.609, -13.310, 150.0, bergerie2, TypeZone.QUARANTAINE));

        Animal b1 = animal("Rokhaya",  "Ladoum",    "F", 68.0, 4, StatutSante.SAIN,           "SN-LDM-0010", bergerie2);
        Animal b2 = animal("Cheikh",   "Ladoum",    "M", 90.0, 5, StatutSante.SAIN,           "SN-LDM-0011", bergerie2);
        Animal b3 = animal("Ndeye Bi", "Djallonke", "F", 28.0, 1, StatutSante.EN_OBSERVATION, "SN-DJL-0001", bergerie2);
        Animal b4 = animal("Seydou",   "Touabire",  "M", 60.0, 3, StatutSante.SAIN,           "SN-TBR-0010", bergerie2);
        animalRepository.save(b1); animalRepository.save(b2);
        animalRepository.save(b3); animalRepository.save(b4);

        initPositionGps(b1, 15.6162, -13.3168, 1.0);
        initPositionGps(b2, 15.6158, -13.3175, 1.4);
        initPositionGps(b3, 15.6165, -13.3162, 0.5);
        initPositionGps(b4, 15.6170, -13.3170, 0.9);

        // ================================================================== Bergerie 3 -- Kaffrine
        Bergerie bergerie3 = new Bergerie();
        bergerie3.setNom("Exploitation Kaffrine Agropastorale");
        bergerie3.setCode("KA-77-II-99");
        bergerie3.setAdresse("Koungheul, Kaffrine");
        bergerie3.setRegion("Kaffrine");
        bergerie3.setSuperficieTotale(980.0);
        bergerie3.setEleveur(eleveur3);
        bergerieRepository.save(bergerie3);

        zoneRepository.save(makeZone("Parcelle Dimba Dieye", 14.097, -14.099, 500.0, bergerie3, TypeZone.PATURAGE));

        Animal c1 = animal("Mariama",  "Warale",    "F", 33.0, 2, StatutSante.SAIN, "SN-WRL-0010", bergerie3);
        Animal c2 = animal("Boubacar", "Peul-Peul", "M", 42.0, 3, StatutSante.SAIN, "SN-PPL-0010", bergerie3);
        animalRepository.save(c1); animalRepository.save(c2);

        initPositionGps(c1, 14.0972, -14.0988, 0.8);
        initPositionGps(c2, 14.0968, -14.0992, 1.1);
    }

    // ------------------------------------------------------------------ Helpers

    private void initPositionGps(Animal animal, double lat, double lon, double vitesse) {
        CapteurAnimal capteur = new CapteurAnimal(null, TypeCapteur.GPS, StatutCapteur.ACTIF, LocalDate.now().minusMonths(1), animal);
        capteur = capteurAnimalRepository.save(capteur);

        Localisation loc = new Localisation();
        loc.setAnimal(animal);
        loc.setCapteur(capteur);
        loc.setLatitude(lat);
        loc.setLongitude(lon);
        loc.setVitesse(vitesse);
        loc.setPrecisionGps(1.8 + Math.random());
        loc.setDateHeure(LocalDateTime.now().minusMinutes((long) (Math.random() * 5)));
        localisationRepository.save(loc);
    }

    private Animal animal(String nom, String race, String sexe, double poids,
                          int ageAns, StatutSante sante, String puce, Bergerie bergerie) {
        Animal a = new Animal();
        a.setNom(nom);
        a.setRace(race);
        a.setSexe(sexe);
        a.setPoids(poids);
        a.setDateNaissance(ageAns > 0
                ? LocalDate.now().minusYears(ageAns)
                : LocalDate.now().minusMonths(6));
        a.setStatutSante(sante);
        a.setNumeroCapteur(puce);
        a.setBergerie(bergerie);
        return a;
    }

    private Zone makeZone(String nom, double lat, double lon, double superficie, Bergerie bergerie, TypeZone type) {
        Zone z = new Zone();
        z.setNom(nom);
        z.setLatitude(lat);
        z.setLongitude(lon);
        z.setSuperficie(superficie);
        z.setBergerie(bergerie);
        z.setTypeZone(type);
        return z;
    }

    private ActeMedical acte(Animal animal, Veterinaire vet, TypeSoin type,
                              String description, String medicament, String dose,
                              LocalDate dateSoin, LocalDate rappel) {
        ActeMedical a = new ActeMedical();
        a.setAnimal(animal);
        a.setUtilisateur(vet);
        a.setTypeSoin(type);
        a.setDescription(description);
        a.setMedicament(medicament);
        a.setDose(dose);
        a.setDateSoin(dateSoin);
        a.setProchainRappel(rappel);
        return a;
    }
}
