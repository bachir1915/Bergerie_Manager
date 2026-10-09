package com.bergerie.repository;

import com.bergerie.entity.Capteur;
import com.bergerie.enums.StatutCapteur;
import com.bergerie.enums.TypeCapteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CapteurRepository extends JpaRepository<Capteur, Long> {

    List<Capteur> findByType(TypeCapteur type);

    List<Capteur> findByTypeAndStatut(TypeCapteur type, StatutCapteur statut);

    List<Capteur> findByStatut(StatutCapteur statut);

    @Query("SELECT ca FROM CapteurAnimal ca WHERE ca.animal.id = :animalId")
    List<Capteur> findByAnimalId(@Param("animalId") Long animalId);

    @Query("SELECT ca FROM CapteurAnimal ca WHERE ca.animal.id = :animalId AND ca.type = :type")
    Optional<Capteur> findByAnimalIdAndType(@Param("animalId") Long animalId, @Param("type") TypeCapteur type);
}
