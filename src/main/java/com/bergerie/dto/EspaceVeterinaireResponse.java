package com.bergerie.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Vue de l'espace vétérinaire focalisée sur une bergerie (celle dont le code a servi à la connexion,
 * ou celle sélectionnée ensuite). Regroupe tout ce dont le vétérinaire a besoin pour suivre le cheptel.
 */
public record EspaceVeterinaireResponse(
        BergerieInfo bergerie,
        long totalAnimaux,
        long animauxMalades,
        long animauxCritiques,
        List<AnimalItem> animaux,
        List<AlerteDto> alertes,
        List<ActeItem> derniersActes,
        List<DonneeEnvDto> dernieresMesures
) {

    public record BergerieInfo(
            Long id,
            String nom,
            String code,
            String region,
            String adresse,
            Double superficieTotale,
            String eleveurNomComplet,
            String eleveurTelephone,
            String eleveurEmail,
            List<String> zones
    ) {
    }

    public record AnimalItem(
            Long id,
            String nom,
            String race,
            String sexe,
            Double poids,
            LocalDate dateNaissance,
            String statut,
            String statutSante,
            String numeroCapteur
    ) {
    }

    public record ActeItem(
            Long id,
            String animalNom,
            String typeSoin,
            String description,
            String medicament,
            String dose,
            LocalDate dateSoin,
            LocalDate prochainRappel
    ) {
    }
}
