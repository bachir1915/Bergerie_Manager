package com.bergerie.config;

import com.bergerie.entity.Bergerie;
import com.bergerie.repository.BergerieRepository;
import com.bergerie.service.BergerieCodeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Attribue un code au format AA-76-VI-77 à toute bergerie qui n'en a pas encore
 * (bergeries créées avant l'ajout de la fonctionnalité). S'exécute à chaque démarrage,
 * sur tous les profils, avant les autres runners.
 */
@Component
@Order(0)
@RequiredArgsConstructor
public class BergerieCodeInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BergerieCodeInitializer.class);

    private final BergerieRepository bergerieRepository;
    private final BergerieCodeService codeService;

    @Override
    public void run(String... args) {
        List<Bergerie> sansCode = bergerieRepository.findAll().stream()
                .filter(b -> b.getCode() == null || b.getCode().isBlank())
                .toList();

        for (Bergerie b : sansCode) {
            b.setCode(codeService.genererCodeUnique());
        }
        if (!sansCode.isEmpty()) {
            bergerieRepository.saveAll(sansCode);
            log.info("Code généré pour {} bergerie(s) existante(s).", sansCode.size());
        }
    }
}
