package com.bergerie.repository;

import com.bergerie.entity.ConnexionVeterinaire;
import com.bergerie.enums.StatutConnexion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnexionVeterinaireRepository extends JpaRepository<ConnexionVeterinaire, Long> {

    Optional<ConnexionVeterinaire> findByVeterinaireIdAndBergerieId(Long veterinaireId, Long bergerieId);

    boolean existsByVeterinaireEmailAndBergerieIdAndStatut(String email, Long bergerieId, StatutConnexion statut);

    // Demandes reçues par un éleveur, pour toutes ses bergeries (les plus récentes d'abord).
    @Query("SELECT c FROM ConnexionVeterinaire c JOIN FETCH c.veterinaire JOIN FETCH c.bergerie b "
            + "WHERE b.eleveur.email = :email ORDER BY c.dateDemande DESC")
    List<ConnexionVeterinaire> findByEleveurEmail(String email);
}
