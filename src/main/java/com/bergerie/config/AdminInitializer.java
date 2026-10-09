package com.bergerie.config;

import com.bergerie.entity.Admin;
import com.bergerie.repository.AdminRepository;
import com.bergerie.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crée le compte ADMIN au premier démarrage en production s'il n'existe pas encore.
 * Il n'y a aucune inscription publique pour ce rôle : c'est le seul point d'entrée.
 *
 * Configurable dans application-prod.properties :
 *     app.admin.email=admin@bergerie.com
 *     app.admin.password=change-me
 */
@Component
@Profile("prod")
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final AdminRepository adminRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@bergerie.com}")
    private String email;

    @Value("${app.admin.password:change-me}")
    private String password;

    // Code de connexion de l'admin (email + code, comme tous les utilisateurs).
    @Value("${app.admin.code:ADM-ROOT-0001}")
    private String code;

    @Value("${app.admin.nom:Admin}")
    private String nom;

    @Value("${app.admin.prenom:Système}")
    private String prenom;

    @Value("${app.admin.telephone:77 000 00 00}")
    private String telephone;

    @Override
    public void run(String... args) {
        if (utilisateurRepository.existsByEmail(email)) {
            return;
        }
        Admin admin = new Admin(null, nom, prenom, email, telephone, passwordEncoder.encode(password));
        admin.setCodeConnexion(code);
        adminRepository.save(admin);
        log.warn("Compte ADMIN créé ({} / code {}). Pensez à changer le code par défaut.", email, code);
    }
}
