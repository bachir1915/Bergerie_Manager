package com.bergerie.service;

import com.bergerie.entity.*;
import com.bergerie.enums.StatutAnimal;
import com.bergerie.enums.StatutSante;
import com.bergerie.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final BergerieRepository bergerieRepository;

    public List<Animal> findAll() {
        return animalRepository.findAll();
    }

    public List<Animal> findByBergerie(Long bergerieId) {
        return animalRepository.findByBergerieId(bergerieId);
    }

    public Animal findById(Long id) {
        return animalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Animal non trouvé avec id: " + id));
    }

    public Animal save(Animal animal) {
        return animalRepository.save(animal);
    }

    public Animal update(Long id, Animal updated) {
        Animal animal = findById(id);
        animal.setNom(updated.getNom());
        animal.setRace(updated.getRace());
        animal.setSexe(updated.getSexe());
        animal.setPoids(updated.getPoids());
        animal.setDateNaissance(updated.getDateNaissance());
        animal.setStatut(updated.getStatut());
        animal.setStatutSante(updated.getStatutSante());
        animal.setNumeroPuce(updated.getNumeroPuce());
        return animalRepository.save(animal);
    }

    public void delete(Long id) {
        animalRepository.deleteById(id);
    }

    public long countTotal() {
        return animalRepository.count();
    }

    public long countByBergerie(Long bergerieId) {
        return animalRepository.countByBergerieId(bergerieId);
    }

    public long countMalades() {
        return animalRepository.countByStatutSante(StatutSante.MALADE);
    }

    public long countCritiques() {
        return animalRepository.countByStatutSante(StatutSante.CRITIQUE);
    }

    public List<Object[]> statsParRace() {
        return animalRepository.countByRace();
    }

    public List<Object[]> statsParSante() {
        return animalRepository.countByStatutSante();
    }
}
