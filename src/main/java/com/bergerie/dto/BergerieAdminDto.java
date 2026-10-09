package com.bergerie.dto;

import com.bergerie.entity.Bergerie;

import java.util.List;

/**
 * Vue détaillée d'une bergerie réservée à l'Admin.
 */
public record BergerieAdminDto(
        Long id,
        String nom,
        String code,
        String adresse,
        String region,
        Double superficieTotale,
        Long eleveurId,
        String eleveurNomComplet,
        String eleveurEmail,
        String eleveurTelephone,
        int nombreZones,
        int nombreAnimaux,
        List<String> zones
) {

    public static BergerieAdminDto from(Bergerie b) {
        var eleveur = b.getEleveur();
        return new BergerieAdminDto(
                b.getId(),
                b.getNom(),
                b.getCode(),
                b.getAdresse(),
                b.getRegion(),
                b.getSuperficieTotale(),
                eleveur != null ? eleveur.getId() : null,
                eleveur != null ? eleveur.getPrenom() + " " + eleveur.getNom() : null,
                eleveur != null ? eleveur.getEmail() : null,
                eleveur != null ? eleveur.getTelephone() : null,
                b.getZones() != null ? b.getZones().size() : 0,
                b.getAnimaux() != null ? b.getAnimaux().size() : 0,
                b.getZones() != null ? b.getZones().stream().map(z -> z.getNom()).toList() : List.of()
        );
    }
}
