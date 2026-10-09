package com.bergerie.dto;

import com.bergerie.entity.Zone;
import com.bergerie.enums.TypeZone;

/**
 * DTO représentant une zone pastorale (géofence) pour l'affichage cartographique.
 */
public record ZoneDto(
        Long id,
        String nom,
        TypeZone typeZone,
        Double latitude,
        Double longitude,
        Double superficie,
        Double rayonMetres,
        Long bergerieId,
        String bergerieNom
) {
    public static ZoneDto from(Zone zone) {
        double rayon = 150.0;
        if (zone.getSuperficie() != null && zone.getSuperficie() > 0) {
            rayon = Math.max(100.0, Math.sqrt(zone.getSuperficie() / Math.PI));
        }
        return new ZoneDto(
                zone.getId(),
                zone.getNom(),
                zone.getTypeZone(),
                zone.getLatitude(),
                zone.getLongitude(),
                zone.getSuperficie(),
                Math.round(rayon * 10.0) / 10.0,
                zone.getBergerie() != null ? zone.getBergerie().getId() : null,
                zone.getBergerie() != null ? zone.getBergerie().getNom() : null
        );
    }
}
