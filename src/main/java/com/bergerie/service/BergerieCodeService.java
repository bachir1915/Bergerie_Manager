package com.bergerie.service;

import com.bergerie.repository.BergerieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Génération et validation du code d'identification d'une bergerie.
 *
 * Format : {@code AA-76-VI-77}
 *   - 2 lettres majuscules
 *   - 2 chiffres
 *   - un chiffre romain (I..XXX)
 *   - 2 chiffres
 */
@Service
@RequiredArgsConstructor
public class BergerieCodeService {

    /** Expression régulière du format attendu, réutilisable côté validation. */
    public static final String REGEX = "^[A-Z]{2}-\\d{2}-(?=[IVXLCDM])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})-\\d{2}$";
    public static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final String LETTRES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String[] ROMAINS = {
        "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
        "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX",
        "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI", "XXVII", "XXVIII", "XXIX", "XXX"
    };
    private static final SecureRandom RANDOM = new SecureRandom();

    private final BergerieRepository bergerieRepository;

    public static boolean estValide(String code) {
        return code != null && PATTERN.matcher(code).matches();
    }

    /** Normalise (trim + majuscules) un code saisi par l'utilisateur. */
    public static String normaliser(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }

    /** Génère un code au bon format, garanti unique en base. */
    public String genererCodeUnique() {
        for (int i = 0; i < 100; i++) {
            String code = genererCode();
            if (!bergerieRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Impossible de générer un code de bergerie unique.");
    }

    private String genererCode() {
        return "" + lettre() + lettre()
             + "-" + deuxChiffres()
             + "-" + ROMAINS[RANDOM.nextInt(ROMAINS.length)]
             + "-" + deuxChiffres();
    }

    private char lettre() {
        return LETTRES.charAt(RANDOM.nextInt(LETTRES.length()));
    }

    private String deuxChiffres() {
        return String.format("%02d", RANDOM.nextInt(100));
    }
}
