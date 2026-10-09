package com.bergerie.controller.api;

import com.bergerie.dto.AnimalDto;
import com.bergerie.dto.AnimalForm;
import com.bergerie.entity.Animal;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.Eleveur;
import com.bergerie.enums.StatutAnimal;
import com.bergerie.enums.StatutSante;
import com.bergerie.repository.AnimalRepository;
import com.bergerie.repository.EleveurRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Gestion du cheptel par l'éleveur : ajout, modification, suppression d'un animal.
 * L'éleveur ne peut agir que sur les animaux de SES bergeries.
 * (La lecture GET /api/animaux est dans ConsultationController.)
 */
@RestController
@RequestMapping("/api/animaux")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ELEVEUR')")
public class AnimalApiController {

    private final AnimalRepository animalRepository;
    private final EleveurRepository eleveurRepository;

    /** Bergeries de l'éleveur, pour alimenter le formulaire. */
    @GetMapping("/mes-bergeries")
    @Transactional(readOnly = true)
    public List<BergerieChoix> mesBergeries(Authentication auth) {
        return eleveur(auth).getBergeries().stream()
                .map(b -> new BergerieChoix(b.getId(), b.getNom(), b.getCode()))
                .toList();
    }

    @PostMapping
    @Transactional
    public AnimalDto creer(@Valid @RequestBody AnimalForm form, Authentication auth) {
        Bergerie bergerie = bergerieDeLEleveur(auth, form.bergerieId());
        Animal a = new Animal();
        appliquer(a, form);
        a.setBergerie(bergerie);
        return AnimalDto.from(animalRepository.save(a));
    }

    @PutMapping("/{id}")
    @Transactional
    public AnimalDto modifier(@PathVariable Long id, @Valid @RequestBody AnimalForm form, Authentication auth) {
        Animal a = animalPossede(auth, id);
        appliquer(a, form);
        if (form.bergerieId() != null) {
            a.setBergerie(bergerieDeLEleveur(auth, form.bergerieId()));
        }
        return AnimalDto.from(animalRepository.save(a));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void supprimer(@PathVariable Long id, Authentication auth) {
        animalRepository.delete(animalPossede(auth, id));
    }

    // ------------------------------------------------------------------

    private void appliquer(Animal a, AnimalForm f) {
        a.setNom(f.nom());
        a.setRace(f.race());
        a.setSexe(f.sexe());
        a.setPoids(f.poids());
        a.setDateNaissance(f.dateNaissance());
        a.setNumeroCapteur(f.numeroCapteur());
        a.setStatut(f.statut() != null ? StatutAnimal.valueOf(f.statut()) : StatutAnimal.ACTIF);
        a.setStatutSante(f.statutSante() != null ? StatutSante.valueOf(f.statutSante()) : StatutSante.SAIN);
    }

    private Eleveur eleveur(Authentication auth) {
        return eleveurRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Éleveur inconnu."));
    }

    private Bergerie bergerieDeLEleveur(Authentication auth, Long bergerieId) {
        if (bergerieId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La bergerie est obligatoire.");
        }
        return eleveur(auth).getBergeries().stream()
                .filter(b -> b.getId().equals(bergerieId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Cette bergerie ne vous appartient pas."));
    }

    private Animal animalPossede(Authentication auth, Long animalId) {
        Animal a = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal introuvable."));
        List<Long> mesIds = eleveur(auth).getBergeries().stream().map(Bergerie::getId).toList();
        if (a.getBergerie() == null || !mesIds.contains(a.getBergerie().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cet animal n'est pas dans vos bergeries.");
        }
        return a;
    }

    public record BergerieChoix(Long id, String nom, String code) {
    }
}
