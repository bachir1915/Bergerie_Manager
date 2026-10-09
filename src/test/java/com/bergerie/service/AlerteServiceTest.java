package com.bergerie.service;

import com.bergerie.entity.Alerte;
import com.bergerie.entity.Animal;
import com.bergerie.entity.Eleveur;
import com.bergerie.entity.Utilisateur;
import com.bergerie.enums.CanalNotif;
import com.bergerie.enums.NiveauAlerte;
import com.bergerie.enums.StatutAlerte;
import com.bergerie.enums.TypeAlerte;
import com.bergerie.repository.AlerteRepository;
import com.bergerie.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires du service de gestion des alertes sanitaires.
 * Vérifie la création, le marquage et le comptage des alertes et notifications.
 */
@ExtendWith(MockitoExtension.class)
class AlerteServiceTest {

    @Mock
    private AlerteRepository alerteRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AlerteService alerteService;

    private Animal animal;
    private Alerte alerte;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        // Préparation d'un animal de test
        animal = new Animal();
        animal.setId(1L);
        animal.setNom("Bella");

        // Préparation d'un utilisateur (éleveur) de test
        utilisateur = new Eleveur();
        utilisateur.setId(10L);
        utilisateur.setEmail("eleveur@test.com");

        // Préparation d'une alerte de test (nouvelle)
        alerte = new Alerte();
        alerte.setId(100L);
        alerte.setAnimal(animal);
        alerte.setType(TypeAlerte.ANOMALIE_SANTE);
        alerte.setMessage("Test alerte");
        alerte.setNiveau(NiveauAlerte.MODERE);
        alerte.setStatutAlerte(StatutAlerte.NOUVELLE);
        alerte.setDateHeure(LocalDateTime.now());
    }

    @Test
    @DisplayName("marquerLue : le statut de l'alerte doit passer à EN_COURS et être sauvegardé")
    void testMarquerLue_doitChangerStatutAEnCours() {
        // Arrange : l'alerte est initialement NOUVELLE
        when(alerteRepository.findById(100L)).thenReturn(Optional.of(alerte));
        when(alerteRepository.save(any(Alerte.class))).thenReturn(alerte);

        // Act
        alerteService.marquerLue(100L);

        // Assert : capture l'objet sauvegardé et vérifie son statut
        ArgumentCaptor<Alerte> captor = ArgumentCaptor.forClass(Alerte.class);
        verify(alerteRepository, times(1)).save(captor.capture());
        assertEquals(StatutAlerte.EN_COURS, captor.getValue().getStatutAlerte(),
                "Le statut devrait être EN_COURS après appel de marquerLue()");
    }

    @Test
    @DisplayName("marquerTraitee : le statut de l'alerte doit passer à TRAITEE")
    void testMarquerTraitee_doitChangerStatutATraitee() {
        // Arrange
        when(alerteRepository.findById(100L)).thenReturn(Optional.of(alerte));
        when(alerteRepository.save(any(Alerte.class))).thenReturn(alerte);

        // Act
        alerteService.marquerTraitee(100L);

        // Assert
        ArgumentCaptor<Alerte> captor = ArgumentCaptor.forClass(Alerte.class);
        verify(alerteRepository, times(1)).save(captor.capture());
        assertEquals(StatutAlerte.TRAITEE, captor.getValue().getStatutAlerte(),
                "Le statut devrait être TRAITEE après appel de marquerTraitee()");
    }

    @Test
    @DisplayName("marquerLue : doit lever une RuntimeException si l'alerte n'existe pas")
    void testMarquerLue_doitLancerExceptionSiInconnu() {
        // Arrange : simuler une alerte introuvable en base
        when(alerteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert : vérifier que l'exception est bien lancée
        assertThrows(RuntimeException.class, () -> alerteService.marquerLue(999L),
                "Une RuntimeException devrait être levée si l'alerte n'existe pas");
        verify(alerteRepository, never()).save(any());
    }

    @Test
    @DisplayName("creerAlerte : doit créer 1 alerte et notifier chaque utilisateur via NotificationService")
    void testCreerAlerte_doitSauvegarderAlerteEtNotifierUtilisateurs() {
        // Arrange : 2 utilisateurs en base
        Utilisateur utilisateur2 = new Eleveur();
        utilisateur2.setId(11L);
        utilisateur2.setEmail("eleveur2@test.com");

        when(utilisateurRepository.findAll()).thenReturn(List.of(utilisateur, utilisateur2));
        when(alerteRepository.save(any(Alerte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        alerteService.creerAlerte(animal, TypeAlerte.ANOMALIE_SANTE,
                "Animal malade", NiveauAlerte.CRITIQUE);

        // Assert : une seule alerte sauvegardée
        verify(alerteRepository, times(1)).save(any(Alerte.class));
        // Une notification par utilisateur (2 ici)
        verify(notificationService, times(2)).creerNotification(any(Alerte.class), any(Utilisateur.class), eq(CanalNotif.PUSH));
    }

    @Test
    @DisplayName("countNonLues : doit déléguer correctement au repository avec StatutAlerte.NOUVELLE")
    void testCountNonLues_doitDeleguerAuRepository() {
        // Arrange
        when(alerteRepository.countByStatutAlerte(StatutAlerte.NOUVELLE)).thenReturn(7L);

        // Act
        long count = alerteService.countNonLues();

        // Assert
        assertEquals(7L, count, "Le comptage doit correspondre à ce que retourne le repository");
        verify(alerteRepository, times(1)).countByStatutAlerte(StatutAlerte.NOUVELLE);
    }
}
