package com.bergerie.repository;

import com.bergerie.entity.Animal;
import com.bergerie.enums.StatutAnimal;
import com.bergerie.enums.StatutSante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findByBergerieId(Long bergerieId);

    List<Animal> findByStatut(StatutAnimal statut);

    List<Animal> findByStatutSante(StatutSante statutSante);

    List<Animal> findByBergerieIdAndStatut(Long bergerieId, StatutAnimal statut);

    long countByBergerieId(Long bergerieId);

    long countByStatutSante(StatutSante statutSante);

    long countByBergerieIdAndStatutSante(Long bergerieId, StatutSante statutSante);

    @Query("SELECT a.race, COUNT(a) FROM Animal a GROUP BY a.race")
    List<Object[]> countByRace();

    @Query("SELECT a.statutSante, COUNT(a) FROM Animal a GROUP BY a.statutSante")
    List<Object[]> countByStatutSante();
}
