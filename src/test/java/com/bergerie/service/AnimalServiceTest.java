package com.bergerie.service;

import com.bergerie.entity.Animal;
import com.bergerie.entity.Bergerie;
import com.bergerie.enums.StatutSante;
import com.bergerie.repository.AnimalRepository;
import com.bergerie.repository.BergerieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires du service de gestion des animaux du troupeau.
 * Vérifie les cas nominaux et les cas d'erreur (animal introuvable, stats sanitaires).
 */
@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private BergerieRepository bergerieRepository;

    @InjectMocks
    private AnimalService animalService;

    private Animal animalSain;
    private Animal animalEnObservation;

    @BeforeEach
    void setUp() {
        Bergerie bergerie = new Bergerie();
        bergerie.setId(1L);
        bergerie.setNom("Al-Baraka");

        animalSain = new Animal();
        animalSain.setId(1L);
        animalSain.setNom("Nour");
        animalSain.setRace("Ouled Djellal");
        animalSain.setSexe("F");
        animalSain.setStatutSante(StatutSante.SAIN);
        animalSain.setBergerie(bergerie);

        animalEnObservation = new Animal();
        animalEnObservation.setId(2L);
        animalEnObservation.setNom("Rayan");
        animalEnObservation.setRace("Hamra");
        animalEnObservation.setSexe("M");
        animalEnObservation.setStatutSante(StatutSante.EN_OBSERVATION);
        animalEnObservation.setBergerie(bergerie);
    }

    @Test
    @DisplayName("findById : doit retourner l'animal si l'ID existe")
    void testFindById_doitRetournerAnimal() {
        // Arrange
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animalSain));

        // Act
        Animal result = animalService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Nour", result.getNom());
        assertEquals(StatutSante.SAIN, result.getStatutSante());
    }

    @Test
    @DisplayName("findById : doit lever une RuntimeException si l'ID est inconnu")
    void testFindById_doitLancerExceptionSiInconnu() {
        // Arrange : aucun animal avec cet ID
        when(animalRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> animalService.findById(999L),
                "Une RuntimeException devrait être levée si l'animal n'existe pas");
        assertTrue(ex.getMessage().contains("999"),
                "Le message d'erreur devrait mentionner l'ID introuvable");
    }

    @Test
    @DisplayName("findAll : doit retourner tous les animaux depuis le repository")
    void testFindAll_doitRetournerTousLesAnimaux() {
        // Arrange
        when(animalRepository.findAll()).thenReturn(List.of(animalSain, animalEnObservation));

        // Act
        List<Animal> result = animalService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(animalRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("countMalades : doit compter uniquement les animaux avec statut EN_OBSERVATION")
    void testCountMalades_doitRetournerBonChiffre() {
        // Arrange
        when(animalRepository.countByStatutSante(StatutSante.EN_OBSERVATION)).thenReturn(3L);

        // Act
        long count = animalService.countMalades();

        // Assert
        assertEquals(3L, count);
        verify(animalRepository, times(1)).countByStatutSante(StatutSante.EN_OBSERVATION);
    }

    @Test
    @DisplayName("update : doit modifier tous les champs de l'animal existant")
    void testUpdate_doitModifierTousLesChamps() {
        // Arrange
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animalSain));
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> inv.getArgument(0));

        Animal modifications = new Animal();
        modifications.setNom("Nour-Modifiée");
        modifications.setRace("Hamra");
        modifications.setSexe("F");
        modifications.setPoids(52.0);
        modifications.setStatutSante(StatutSante.EN_OBSERVATION);
        modifications.setStatut(animalSain.getStatut());

        // Act
        Animal result = animalService.update(1L, modifications);

        // Assert
        assertEquals("Nour-Modifiée", result.getNom());
        assertEquals("Hamra", result.getRace());
        assertEquals(52.0, result.getPoids());
        assertEquals(StatutSante.EN_OBSERVATION, result.getStatutSante());
    }

    @Test
    @DisplayName("delete : doit appeler deleteById du repository avec le bon ID")
    void testDelete_doitAppelerRepository() {
        // Arrange
        doNothing().when(animalRepository).deleteById(1L);

        // Act
        animalService.delete(1L);

        // Assert
        verify(animalRepository, times(1)).deleteById(1L);
    }
}
