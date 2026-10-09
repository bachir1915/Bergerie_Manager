package com.bergerie.dto;

import com.bergerie.entity.Animal;

import java.time.LocalDate;

public record AnimalDto(
        Long id,
        String nom,
        String race,
        String sexe,
        Double poids,
        LocalDate dateNaissance,
        String statut,
        String statutSante,
        String numeroCapteur,
        String bergerieNom
) {

    public static AnimalDto from(Animal a) {
        return new AnimalDto(
                a.getId(), a.getNom(), a.getRace(), a.getSexe(), a.getPoids(),
                a.getDateNaissance(),
                a.getStatut() != null ? a.getStatut().name() : null,
                a.getStatutSante() != null ? a.getStatutSante().name() : null,
                a.getNumeroCapteur(),
                a.getBergerie() != null ? a.getBergerie().getNom() : null);
    }
}
