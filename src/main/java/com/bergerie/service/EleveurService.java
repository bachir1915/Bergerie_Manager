package com.bergerie.service;

import com.bergerie.entity.Eleveur;
import com.bergerie.repository.EleveurRepository;
import com.bergerie.repository.UtilisateurRepository;
import com.bergerie.util.Telephone;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Inscription publique d'un éleveur. Contrairement au vétérinaire, le compte est
 * actif immédiatement (aucune validation Admin, aucun justificatif).
 * L'éleveur se connecte avec son email + son code personnel ({@code ELV-000000}).
 */
@Service
@RequiredArgsConstructor
public class EleveurService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final EleveurRepository eleveurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CodeConnexionService codeConnexionService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Eleveur inscrire(String nom, String prenom, String email, String telephone) {
        if (utilisateurRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé.");
        }
        Eleveur eleveur = new Eleveur(null, nom, prenom, email, Telephone.normaliser(telephone),
                // Pas de mot de passe : connexion par email + code personnel.
                passwordEncoder.encode(UUID.randomUUID().toString()), genererNumEleveur());
        eleveur.setCodeConnexion(codeConnexionService.genererCodeEleveur());
        return eleveurRepository.save(eleveur);
    }

    private String genererNumEleveur() {
        for (int i = 0; i < 100; i++) {
            String num = "ELV-" + String.format("%04d", RANDOM.nextInt(10_000));
            if (!eleveurRepository.existsByNumEleveur(num)) {
                return num;
            }
        }
        throw new IllegalStateException("Impossible de générer un numéro d'éleveur unique.");
    }
}
