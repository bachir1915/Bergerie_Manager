package com.bergerie.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Corps de requête pour le rejet ou la suspension d'un compte vétérinaire : le motif est obligatoire.
 */
public record MotifRequest(@NotBlank String motif) {
}
