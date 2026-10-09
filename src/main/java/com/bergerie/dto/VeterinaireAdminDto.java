package com.bergerie.dto;

import com.bergerie.entity.Veterinaire;

import java.time.LocalDateTime;

/**
 * Vue complète d'un vétérinaire réservée à l'Admin.
 * Contient l'email et les métadonnées du justificatif (jamais exposées aux éleveurs).
 */
public record VeterinaireAdminDto(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone,
        String specialite,
        String statutValidation,
        String motifSuspension,
        LocalDateTime dateCreation,
        boolean justificatifPresent,
        String justificatifNomFichier,
        String justificatifTypeContenu,
        String justificatifUrl
) {

    public static VeterinaireAdminDto from(Veterinaire v) {
        return new VeterinaireAdminDto(
                v.getId(),
                v.getNom(),
                v.getPrenom(),
                v.getEmail(),
                v.getTelephone(),
                v.getSpecialite(),
                v.getStatutValidation().name(),
                v.getMotifSuspension(),
                v.getDateCreation(),
                v.aUnJustificatif(),
                v.getJustificatifNomFichier(),
                v.getJustificatifTypeContenu(),
                v.aUnJustificatif() ? "/api/admin/veterinaires/" + v.getId() + "/justificatif" : null
        );
    }
}
