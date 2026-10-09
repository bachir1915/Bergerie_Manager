package com.bergerie.service;

import com.bergerie.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * Génère et valide les codes personnels de connexion.
 *
 *  - ADMIN   : {@code ADM-XXXX-XXXX}  (X = lettre majuscule ou chiffre)
 *  - ELEVEUR : {@code ELV-000000}     (6 chiffres)
 *
 * Le VÉTÉRINAIRE n'a pas de code personnel : il se connecte avec le code d'une bergerie
 * (voir {@link BergerieCodeService}).
 */
@Service
@RequiredArgsConstructor
public class CodeConnexionService {

    public static final Pattern FORMAT_ADMIN = Pattern.compile("^ADM-[A-Z0-9]{4}-[A-Z0-9]{4}$");
    public static final Pattern FORMAT_ELEVEUR = Pattern.compile("^ELV-\\d{6}$");

    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UtilisateurRepository utilisateurRepository;

    public static String normaliser(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }

    public String genererCodeEleveur() {
        return genererUnique(() -> "ELV-" + String.format("%06d", RANDOM.nextInt(1_000_000)));
    }

    public String genererCodeAdmin() {
        return genererUnique(() -> "ADM-" + bloc(4) + "-" + bloc(4));
    }

    private String genererUnique(java.util.function.Supplier<String> generateur) {
        for (int i = 0; i < 100; i++) {
            String code = generateur.get();
            if (!utilisateurRepository.existsByCodeConnexion(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Impossible de générer un code de connexion unique.");
    }

    private String bloc(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }
        return sb.toString();
    }
}
