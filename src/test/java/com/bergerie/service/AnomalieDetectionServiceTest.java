package com.bergerie.service;

import com.bergerie.client.MlPredictionClient;
import com.bergerie.dto.ml.CapteurLectureDTO;
import com.bergerie.dto.ml.PredictionAnomalieDTO;
import com.bergerie.entity.Alerte;
import com.bergerie.entity.Animal;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.Eleveur;
import com.bergerie.entity.Notification;
import com.bergerie.entity.Veterinaire;
import com.bergerie.enums.StatutAnimal;
import com.bergerie.enums.StatutSante;
import com.bergerie.enums.StatutValidation;
import com.bergerie.repository.AlerteRepository;
import com.bergerie.repository.AnimalRepository;
import com.bergerie.repository.DonneesEnvRepository;
import com.bergerie.repository.LocalisationRepository;
import com.bergerie.repository.NotificationRepository;
import com.bergerie.repository.VeterinaireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnomalieDetectionServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private LocalisationRepository localisationRepository;

    @Mock
    private DonneesEnvRepository donneesEnvRepository;

    @Mock
    private AlerteRepository alerteRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private VeterinaireRepository veterinaireRepository;

    @Mock
    private MlPredictionClient mlPredictionClient;

    @InjectMocks
    private AnomalieDetectionService anomalieDetectionService;

    private Animal animal;
    private Bergerie bergerie;
    private Eleveur eleveur;
    private Veterinaire veterinaireValide;

    @BeforeEach
    void setUp() {
        eleveur = new Eleveur();
        eleveur.setId(10L);
        eleveur.setEmail("eleveur@test.com");
        eleveur.setNom("Diallo");
        eleveur.setPrenom("Mamadou");

        bergerie = new Bergerie();
        bergerie.setId(1L);
        bergerie.setNom("Bergerie du Soleil");
        bergerie.setEleveur(eleveur);

        animal = new Animal();
        animal.setId(42L);
        animal.setNom("Bella");
        animal.setStatut(StatutAnimal.ACTIF);
        animal.setStatutSante(StatutSante.SAIN);
        animal.setBergerie(bergerie);

        veterinaireValide = new Veterinaire();
        veterinaireValide.setId(20L);
        veterinaireValide.setEmail("vet@test.com");
        veterinaireValide.setNom("Sow");
        veterinaireValide.setStatutValidation(StatutValidation.VALIDE);
    }

    @Test
    @DisplayName("Cas normal : état sanitaire sain -> Aucune alerte n'est générée")
    void testLectureNormale_AucuneAlerteCreee() {
        LocalDateTime maintenant = LocalDateTime.of(2026, 9, 8, 14, 0);

        PredictionAnomalieDTO predNormale = PredictionAnomalieDTO.builder()
                .animalId(42L)
                .scoreAnomalie(0.42)
                .estAnomalie(false)
                .niveauRisque("NORMAL")
                .details("Comportement circadien conforme.")
                .build();

        when(localisationRepository.findByAnimalIdAndDateHeureAfterOrderByDateHeureAsc(eq(42L), any()))
                .thenReturn(Collections.emptyList());
        when(donneesEnvRepository.findRecentByBergerieId(eq(1L), any()))
                .thenReturn(Collections.emptyList());
        when(mlPredictionClient.predireAnomalie(any(CapteurLectureDTO.class)))
                .thenReturn(Optional.of(predNormale));

        Optional<PredictionAnomalieDTO> resultat = anomalieDetectionService.analyserAnimal(animal, maintenant);

        assertTrue(resultat.isPresent());
        assertFalse(resultat.get().getEstAnomalie());
        assertEquals("NORMAL", resultat.get().getNiveauRisque());

        // Vérifie qu'aucune alerte n'a été insérée en base
        verify(alerteRepository, never()).save(any(Alerte.class));
        verify(notificationRepository, never()).save(any(Notification.class));
        assertEquals(StatutSante.SAIN, animal.getStatutSante());
    }

    @Test
    @DisplayName("Cas anormal (immobilité diurne) : risque détecté -> 1 Alerte et 2 Notifications créées")
    void testImmobiliteDiurne_DeclenchementAlerteEtNotifications() {
        LocalDateTime maintenant = LocalDateTime.of(2026, 9, 8, 14, 0);

        PredictionAnomalieDTO predAnormale = PredictionAnomalieDTO.builder()
                .animalId(42L)
                .scoreAnomalie(0.68)
                .estAnomalie(true)
                .niveauRisque("CRITIQUE")
                .details("Immobilité diurne suspecte à 14h")
                .build();

        when(localisationRepository.findByAnimalIdAndDateHeureAfterOrderByDateHeureAsc(eq(42L), any()))
                .thenReturn(Collections.emptyList());
        when(donneesEnvRepository.findRecentByBergerieId(eq(1L), any()))
                .thenReturn(Collections.emptyList());
        when(veterinaireRepository.findByStatutValidationOrderByNomAsc(StatutValidation.VALIDE))
                .thenReturn(List.of(veterinaireValide));
        when(mlPredictionClient.predireAnomalie(any(CapteurLectureDTO.class)))
                .thenReturn(Optional.of(predAnormale));
        when(alerteRepository.save(any(Alerte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<PredictionAnomalieDTO> resultat = anomalieDetectionService.analyserAnimal(animal, maintenant);

        assertTrue(resultat.isPresent());
        assertTrue(resultat.get().getEstAnomalie());
        assertEquals("CRITIQUE", resultat.get().getNiveauRisque());

        // Vérifie 1 sauvegarde d'alerte générale
        verify(alerteRepository, times(1)).save(any(Alerte.class));

        // Vérifie 2 sauvegardes de notifications (1 éleveur + 1 vétérinaire)
        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(notifCaptor.capture());
        assertEquals(2, notifCaptor.getAllValues().size());

        // Vérifie que le statut de santé de l'animal est passé à CRITIQUE
        assertEquals(StatutSante.CRITIQUE, animal.getStatutSante());
        verify(animalRepository, times(1)).save(animal);
    }

    @Test
    @DisplayName("Service Python indisponible (503 ou panne réseau) -> Pas de crash, aucune alerte erronée")
    void testMicroserviceIndisponible_NePlantePas() {
        LocalDateTime maintenant = LocalDateTime.of(2026, 9, 8, 14, 0);

        when(localisationRepository.findByAnimalIdAndDateHeureAfterOrderByDateHeureAsc(eq(42L), any()))
                .thenReturn(Collections.emptyList());
        when(donneesEnvRepository.findRecentByBergerieId(eq(1L), any()))
                .thenReturn(Collections.emptyList());
        // Simulation d'une panne ou timeout du service Python
        when(mlPredictionClient.predireAnomalie(any(CapteurLectureDTO.class)))
                .thenReturn(Optional.empty());

        Optional<PredictionAnomalieDTO> resultat = anomalieDetectionService.analyserAnimal(animal, maintenant);

        // Doit renvoyer un Optional vide sans lever d'exception
        assertTrue(resultat.isEmpty());

        // Aucune fausse alerte ne doit être créée
        verify(alerteRepository, never()).save(any(Alerte.class));
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}
