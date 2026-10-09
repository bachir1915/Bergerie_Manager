package com.bergerie.dto;

/**
 * Connexion : email + code (au lieu d'un mot de passe).
 * Le code dépend du rôle :
 *   - ADMIN   : code personnel  ADM-XXXX-XXXX
 *   - ELEVEUR : code personnel  ELV-000000
 *   - VETERINAIRE : code d'une bergerie  AA-76-VI-77
 */
public record LoginRequest(String email, String code) {
}
