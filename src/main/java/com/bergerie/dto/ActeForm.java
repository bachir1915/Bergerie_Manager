package com.bergerie.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Formulaire de saisie d'un acte médical par le vétérinaire.
 */
public record ActeForm(
        @NotNull Long animalId,
        @NotNull String typeSoin,
        String description,
        String medicament,
        String dose,
        @NotNull LocalDate dateSoin,
        LocalDate prochainRappel
) {
}
