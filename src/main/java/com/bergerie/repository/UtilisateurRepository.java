package com.bergerie.repository;

import com.bergerie.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<Utilisateur> findByCodeConnexion(String codeConnexion);
    boolean existsByCodeConnexion(String codeConnexion);
}
