package com.bergerie.repository;

import com.bergerie.entity.Capteur;
import com.bergerie.enums.TypeCapteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CapteurRepository extends JpaRepository<Capteur, Long> {
    List<Capteur> findByAnimalId(Long animalId);
    List<Capteur> findByZoneId(Long zoneId);
    Optional<Capteur> findByAnimalIdAndType(Long animalId, TypeCapteur type);
}
