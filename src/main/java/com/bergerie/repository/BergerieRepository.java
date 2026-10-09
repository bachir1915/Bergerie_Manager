package com.bergerie.repository;

import com.bergerie.entity.Bergerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BergerieRepository extends JpaRepository<Bergerie, Long> {

    // Côté Admin : toutes les bergeries avec leur éleveur déjà chargé (évite le N+1 sur eleveur).
    // Les collections zones / animaux restent lazy et sont parcourues dans une méthode @Transactional.
    @Query("select distinct b from Bergerie b left join fetch b.eleveur order by b.nom asc")
    List<Bergerie> findAllAvecEleveur();

    // Connexion vétérinaire par code de bergerie.
    Optional<Bergerie> findByCode(String code);

    boolean existsByCode(String code);
}
