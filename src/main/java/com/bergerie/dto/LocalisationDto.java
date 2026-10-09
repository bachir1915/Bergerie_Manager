package com.bergerie.dto;

import com.bergerie.entity.Localisation;
import com.bergerie.enums.StatutSante;
import java.time.LocalDateTime;

/**
 * DTO de localisation GPS exposé à l'API REST Angular.
 * Contient les données nécessaires pour afficher les marqueurs sur la carte Leaflet.
 */
public record LocalisationDto(
        Long id,
        Long animalId,
        String animalNom,
        String animalRace,
        StatutSante animalStatutSante,
        Double latitude,
        Double longitude,
        LocalDateTime dateHeure,
        Double vitesse
) {
    /** Convertit une entité Localisation en DTO de présentation. */
    public static LocalisationDto from(Localisation loc) {
        return new LocalisationDto(
                loc.getId(),
                loc.getAnimal().getId(),
                loc.getAnimal().getNom(),
                loc.getAnimal().getRace(),
                loc.getAnimal().getStatutSante(),
                loc.getLatitude(),
                loc.getLongitude(),
                loc.getDateHeure(),
                loc.getVitesse()
        );
    }
}
