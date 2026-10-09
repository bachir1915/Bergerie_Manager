package com.bergerie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Inscription publique d'un éleveur (compte actif immédiatement, sans validation Admin).
 * Aucun mot de passe : un code personnel de connexion est généré et renvoyé.
 */
public record InscriptionEleveurRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        @NotBlank @Email String email,
        @NotBlank String telephone
) {
}
