package com.bergerie.enums;

/**
 * Statut de validation du compte d'un vétérinaire par l'Admin.
 *
 * - EN_ATTENTE : valeur par défaut à l'inscription, le compte n'est pas encore vérifié.
 * - VALIDE     : l'Admin a vérifié le justificatif, le vétérinaire a accès aux fonctionnalités métier.
 * - REJETE     : l'inscription a été refusée (motif obligatoire).
 * - SUSPENDU   : un compte auparavant validé a été désactivé (motif obligatoire).
 *
 * Seul le statut VALIDE donne accès aux fonctionnalités sensibles de la plateforme.
 */
public enum StatutValidation {
    EN_ATTENTE,
    VALIDE,
    REJETE,
    SUSPENDU
}
