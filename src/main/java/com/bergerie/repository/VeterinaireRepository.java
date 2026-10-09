package com.bergerie.repository;

import com.bergerie.entity.Veterinaire;
import com.bergerie.enums.StatutValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinaireRepository extends JpaRepository<Veterinaire, Long> {

    Optional<Veterinaire> findByEmail(String email);

    // Côté Admin : liste filtrée par statut (ex : EN_ATTENTE pour traiter les nouvelles inscriptions).
    List<Veterinaire> findByStatutValidationOrderByDateCreationDesc(StatutValidation statutValidation);

    // Côté Éleveur : uniquement les vétérinaires dont le compte est VALIDE.
    List<Veterinaire> findByStatutValidationOrderByNomAsc(StatutValidation statutValidation);
}
