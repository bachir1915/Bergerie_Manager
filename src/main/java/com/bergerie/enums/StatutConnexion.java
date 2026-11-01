package com.bergerie.enums;

/**
 * Etat de la liaison entre un vétérinaire et une bergerie.
 * Le vétérinaire demande l'accès avec le code de la bergerie ; l'éleveur l'accepte, la refuse ou l'annule.
 */
public enum StatutConnexion {
    EN_ATTENTE,
    ACCEPTEE,
    REFUSEE,
    ANNULEE
}
