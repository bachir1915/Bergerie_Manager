package com.bergerie.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Numéro de téléphone au format {@code 77 123 45 67} (9 chiffres, groupés 2-3-2-2).
 */
public final class Telephone {

    private Telephone() {
    }

    /**
     * Valide puis normalise un numéro saisi (avec ou sans espaces) vers {@code 77 123 45 67}.
     * @throws ResponseStatusException 400 si le numéro n'a pas exactement 9 chiffres.
     */
    public static String normaliser(String saisie) {
        String chiffres = saisie == null ? "" : saisie.replaceAll("\\D", "");
        if (chiffres.length() != 9) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Numéro de téléphone invalide (format attendu : 77 123 45 67).");
        }
        return chiffres.substring(0, 2) + " " + chiffres.substring(2, 5) + " "
             + chiffres.substring(5, 7) + " " + chiffres.substring(7, 9);
    }

    public static boolean estValide(String saisie) {
        String chiffres = saisie == null ? "" : saisie.replaceAll("\\D", "");
        return chiffres.length() == 9;
    }
}
