package com.bergerie.config;

import com.bergerie.entity.ConnexionVeterinaire;
import com.bergerie.enums.StatutConnexion;
import com.bergerie.enums.StatutValidation;
import com.bergerie.repository.BergerieRepository;
import com.bergerie.repository.ConnexionVeterinaireRepository;
import com.bergerie.repository.VeterinaireRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Données de démonstration des connexions vétérinaire - bergerie (idempotent).
 *  - vet@bergerie.com : connexion ACCEPTEE sur chaque bergerie (le compte de démo peut se connecter) ;
 *  - diop.moussa.vet@gmail.com : demande EN_ATTENTE sur la bergerie AB-12-IV-34, que l'éleveur peut accepter ou refuser.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
@Slf4j
public class ConnexionInitializer implements CommandLineRunner {

    private final VeterinaireRepository veterinaireRepository;
    private final BergerieRepository bergerieRepository;
    private final ConnexionVeterinaireRepository connexionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        veterinaireRepository.findByEmail("vet@bergerie.com").ifPresent(vet -> {
            if (vet.getStatutValidation() != StatutValidation.VALIDE) {
                return;
            }
            for (var b : bergerieRepository.findAll()) {
                if (connexionRepository.findByVeterinaireIdAndBergerieId(vet.getId(), b.getId()).isEmpty()) {
                    creer(vet, b, StatutConnexion.ACCEPTEE);
                }
            }
        });
        veterinaireRepository.findByEmail("diop.moussa.vet@gmail.com").ifPresent(vet -> {
            if (vet.getStatutValidation() != StatutValidation.VALIDE) {
                return;
            }
            bergerieRepository.findByCode("AB-12-IV-34").ifPresent(b -> {
                if (connexionRepository.findByVeterinaireIdAndBergerieId(vet.getId(), b.getId()).isEmpty()) {
                    creer(vet, b, StatutConnexion.EN_ATTENTE);
                }
            });
        });
    }

    private void creer(com.bergerie.entity.Veterinaire vet, com.bergerie.entity.Bergerie b, StatutConnexion statut) {
        ConnexionVeterinaire c = new ConnexionVeterinaire();
        c.setVeterinaire(vet);
        c.setBergerie(b);
        c.setStatut(statut);
        c.setDateDemande(LocalDateTime.now());
        if (statut == StatutConnexion.ACCEPTEE) {
            c.setDateReponse(LocalDateTime.now());
        }
        connexionRepository.save(c);
        log.info("[CONNEXION] {} <-> {} : {}", vet.getEmail(), b.getCode(), statut);
    }
}
