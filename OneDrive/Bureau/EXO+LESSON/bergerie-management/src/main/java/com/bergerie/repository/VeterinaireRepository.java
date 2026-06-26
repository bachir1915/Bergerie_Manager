package com.bergerie.repository;

import com.bergerie.entity.Veterinaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VeterinaireRepository extends JpaRepository<Veterinaire, Long> {
    Optional<Veterinaire> findByEmail(String email);
    Optional<Veterinaire> findByNumeroOrdre(String numeroOrdre);
}
