package com.bergerie.dto;

import com.bergerie.entity.ConnexionVeterinaire;

import java.time.LocalDateTime;

/** Demande de connexion d'un vétérinaire à une bergerie, vue par l'éleveur. */
public record ConnexionDto(
        Long id,
        String veterinaireNom,
        String veterinaireEmail,
        String veterinaireTelephone,
        String specialite,
        String bergerieNom,
        String bergerieCode,
        String statut,
        LocalDateTime dateDemande,
        LocalDateTime dateReponse
) {
    public static ConnexionDto from(ConnexionVeterinaire c) {
        var v = c.getVeterinaire();
        return new ConnexionDto(c.getId(), v.getPrenom() + " " + v.getNom(), v.getEmail(), v.getTelephone(),
                v.getSpecialite(), c.getBergerie().getNom(), c.getBergerie().getCode(),
                c.getStatut().name(), c.getDateDemande(), c.getDateReponse());
    }
}
