package com.bergerie.controller;

import com.bergerie.entity.ActeMedical;
import com.bergerie.entity.Animal;
import com.bergerie.entity.Utilisateur;
import com.bergerie.enums.TypeSoin;
import com.bergerie.repository.UtilisateurRepository;
import com.bergerie.service.ActeMedicalService;
import com.bergerie.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/actes-medicaux")
// @RequestMapping: permet de mapper les requêtes HTTP aux méthodes du contrôleur
// @RequiredArgsConstructor: permet de créer un constructeur avec les arguments obligatoires
// @PreAuthorize: permet de sécuriser les méthodes du contrôleur
@RequiredArgsConstructor
public class ActeMedicalController {

    private final ActeMedicalService acteMedicalService;
    private final AnimalService animalService;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping
    public String listActes(Model model) {
        model.addAttribute("actes", acteMedicalService.findAll());
        model.addAttribute("rappels", acteMedicalService.rappelsProchains());
        return "acte-medical/list";
    }

    @GetMapping("/animal/{animalId}")
    public String listActesByAnimal(@PathVariable Long animalId, Model model) {
        Animal animal = animalService.findById(animalId);
        model.addAttribute("animal", animal);
        model.addAttribute("actes", acteMedicalService.findByAnimal(animalId));
        return "acte-medical/list";
    }

    @GetMapping("/nouveau")
    @PreAuthorize("hasRole('VETERINAIRE')")
    public String formNouvelActe(Model model,
                                  @RequestParam(required = false) Long animalId) {
        model.addAttribute("acte", new ActeMedical());
        model.addAttribute("animaux", animalService.findAll());
        model.addAttribute("typesSoin", TypeSoin.values());
        model.addAttribute("selectedAnimalId", animalId);
        return "acte-medical/form";
    }

    @PostMapping("/nouveau")
    @PreAuthorize("hasRole('VETERINAIRE')")
    public String ajouterActe(@ModelAttribute ActeMedical acte,
                               @RequestParam Long animalId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        Animal animal = animalService.findById(animalId);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(authentication.getName()).orElseThrow();

        acte.setAnimal(animal);
        acte.setUtilisateur(utilisateur);
        acteMedicalService.save(acte);
        redirectAttributes.addFlashAttribute("success", "Acte médical enregistré avec succès !");
        return "redirect:/actes-medicaux";
    }

    @PostMapping("/supprimer/{id}")
    @PreAuthorize("hasRole('VETERINAIRE')")
    public String supprimerActe(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        acteMedicalService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Acte médical supprimé.");
        return "redirect:/actes-medicaux";
    }
}
