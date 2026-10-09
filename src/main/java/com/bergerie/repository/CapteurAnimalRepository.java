package com.bergerie.repository;

import com.bergerie.entity.CapteurAnimal;
import com.bergerie.enums.StatutCapteur;
import com.bergerie.enums.TypeCapteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapteurAnimalRepository extends JpaRepository<CapteurAnimal, Long> {
    List<CapteurAnimal> findByAnimalId(Long animalId);
    Optional<CapteurAnimal> findByAnimalIdAndType(Long animalId, TypeCapteur type);
    List<CapteurAnimal> findByAnimalIdAndStatut(Long animalId, StatutCapteur statut);
}
