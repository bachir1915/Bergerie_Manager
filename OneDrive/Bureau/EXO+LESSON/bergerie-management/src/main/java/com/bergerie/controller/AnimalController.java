package com.bergerie.controller;

import com.bergerie.entity.Animal;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.Eleveur;
import com.bergerie.enums.StatutAnimal;
import com.bergerie.enums.StatutSante;
import com.bergerie.repository.BergerieRepository;
import com.bergerie.repository.EleveurRepository;
import com.bergerie.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/animaux")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;
    private final BergerieRepository bergerieRepository;
    private final EleveurRepository eleveurRepository;

    @GetMapping
    public String listAnimaux(Model model, Authentication authentication) {
        String email = authentication.getName();
        boolean isEleveur = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ELEVEUR"));

        List<Animal> animaux;
        if (isEleveur) {
            Eleveur eleveur = eleveurRepository.findByEmail(email).orElseThrow();
            if (eleveur.getBergerie() != null) {
                animaux = animalService.findByBergerie(eleveur.getBergerie().getId());
            } else {
                animaux = animalService.findAll();
            }
        } else {
            animaux = animalService.findAll();
        }

        model.addAttribute("animaux", animaux);
        model.addAttribute("statutsSante", StatutSante.values());
        model.addAttribute("statuts", StatutAnimal.values());
        return "animal/list";
    }

    @GetMapping("/{id}")
    public String detailAnimal(@PathVariable Long id, Model model) {
        Animal animal = animalService.findById(id);
        model.addAttribute("animal", animal);
        return "animal/detail";
    }

    @GetMapping("/nouveau")
    public String formNouvelAnimal(Model model) {
        model.addAttribute("animal", new Animal());
        model.addAttribute("bergeries", bergerieRepository.findAll());
        model.addAttribute("sexes", List.of("M", "F"));
        model.addAttribute("statuts", StatutAnimal.values());
        model.addAttribute("statutsSante", StatutSante.values());
        model.addAttribute("action", "Ajouter");
        return "animal/form";
    }

    @PostMapping("/nouveau")
    public String ajouterAnimal(@ModelAttribute Animal animal,
                                 @RequestParam Long bergerieId,
                                 RedirectAttributes redirectAttributes) {
        Bergerie bergerie = bergerieRepository.findById(bergerieId)
            .orElseThrow(() -> new RuntimeException("Bergerie non trouvée"));
        animal.setBergerie(bergerie);
        animalService.save(animal);
        redirectAttributes.addFlashAttribute("success", "Animal ajouté avec succès !");
        return "redirect:/animaux";
    }

    @GetMapping("/modifier/{id}")
    public String formModifierAnimal(@PathVariable Long id, Model model) {
        Animal animal = animalService.findById(id);
        model.addAttribute("animal", animal);
        model.addAttribute("bergeries", bergerieRepository.findAll());
        model.addAttribute("sexes", List.of("M", "F"));
        model.addAttribute("statuts", StatutAnimal.values());
        model.addAttribute("statutsSante", StatutSante.values());
        model.addAttribute("action", "Modifier");
        return "animal/form";
    }

    @PostMapping("/modifier/{id}")
    public String modifierAnimal(@PathVariable Long id,
                                  @ModelAttribute Animal animal,
                                  @RequestParam Long bergerieId,
                                  RedirectAttributes redirectAttributes) {
        Bergerie bergerie = bergerieRepository.findById(bergerieId)
            .orElseThrow(() -> new RuntimeException("Bergerie non trouvée"));
        animal.setBergerie(bergerie);
        animalService.update(id, animal);
        redirectAttributes.addFlashAttribute("success", "Animal modifié avec succès !");
        return "redirect:/animaux";
    }

    @PostMapping("/supprimer/{id}")
    public String supprimerAnimal(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        animalService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Animal supprimé.");
        return "redirect:/animaux";
    }
}
