package com.bergerie.repository;

import com.bergerie.entity.Eleveur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EleveurRepository extends JpaRepository<Eleveur, Long> {
    Optional<Eleveur> findByEmail(String email);
    Optional<Eleveur> findByNumEleveur(String numEleveur);
}
