package com.bergerie.repository;

import com.bergerie.entity.Localisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocalisationRepository extends JpaRepository<Localisation, Long> {

    List<Localisation> findByAnimalIdOrderByDateHeureDesc(Long animalId);

    @Query("SELECT l FROM Localisation l WHERE l.animal.id = :animalId ORDER BY l.dateHeure DESC LIMIT 1")
    Optional<Localisation> findLatestByAnimalId(Long animalId);

    @Query("SELECT l FROM Localisation l WHERE l.id IN " +
           "(SELECT MAX(l2.id) FROM Localisation l2 GROUP BY l2.animal.id)")
    List<Localisation> findLatestForAllAnimals();

    List<Localisation> findByAnimalIdAndDateHeureAfterOrderByDateHeureAsc(Long animalId, java.time.LocalDateTime after);
}
