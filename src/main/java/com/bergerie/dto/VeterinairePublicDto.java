package com.bergerie.dto;

import com.bergerie.entity.Veterinaire;

/**
 * Vue publique d'un vétérinaire VALIDE, destinée aux éleveurs pour prendre contact.
 */
public record VeterinairePublicDto(
        Long id,
        String nom,
        String prenom,
        String specialite,
        String telephone,
        String email
) {

    public static VeterinairePublicDto from(Veterinaire v) {
        return new VeterinairePublicDto(
                v.getId(),
                v.getNom(),
                v.getPrenom(),
                v.getSpecialite(),
                v.getTelephone(),
                v.getEmail()
        );
    }
}
