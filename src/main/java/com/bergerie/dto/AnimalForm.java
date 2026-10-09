package com.bergerie.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * Formulaire de création / modification d'un animal (côté éleveur).
 */
public record AnimalForm(
        @NotBlank String nom,
        @NotBlank String race,
        @NotBlank String sexe,
        Double poids,
        LocalDate dateNaissance,
        String statut,
        String statutSante,
        String numeroCapteur,
        Long bergerieId
) {
}
