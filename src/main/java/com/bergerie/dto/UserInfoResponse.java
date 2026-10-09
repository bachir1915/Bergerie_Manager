package com.bergerie.dto;

/**
 * Infos de l'utilisateur connecté.
 * {@code statutValidation} n'est renseigné que pour les vétérinaires (sinon null) :
 * il permet au front d'afficher un message si le compte n'est pas encore VALIDE.
 */
public record UserInfoResponse(Long id, String role, String nom, String prenom, String email,
                               String statutValidation) {
}
