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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// @Controller : Indique à Spring que cette classe est un contrôleur MVC.
//               Elle reçoit les requêtes HTTP et retourne des noms de vues (fichiers HTML Thymeleaf).
//               Différent de @RestController qui retourne du JSON.
@Controller

// @RequestMapping("/actes-medicaux") : Toutes les URL gérées par ce contrôleur
//                                       commencent par "/actes-medicaux".
//                                       C'est le préfixe commun à toutes les méthodes de la classe.
@RequestMapping("/actes-medicaux")

// @RequiredArgsConstructor (Lombok) : Génère un constructeur avec les champs "final".
//                                      Spring utilise ce constructeur pour injecter
//                                      automatiquement ActeMedicalService, AnimalService, etc.
//                                      C'est l'injection de dépendances par constructeur.
@RequiredArgsConstructor
public class ActeMedicalController {

    private final ActeMedicalService acteMedicalService;
    private final AnimalService animalService;
    private final UtilisateurRepository utilisateurRepository;

    // @GetMapping : Écoute les requêtes HTTP GET sur "/actes-medicaux".
    //               Utilisé pour AFFICHER des données (lecture seule).
    @GetMapping
    public String listActes(Model model) {
        model.addAttribute("actes", acteMedicalService.findAll());
        model.addAttribute("rappels", acteMedicalService.rappelsProchains());
        return "acte-medical/list";
    }

    // @GetMapping("/animal/{animalId}") : Écoute GET sur "/actes-medicaux/animal/42" par exemple.
    // @PathVariable : Extrait la valeur "{animalId}" directement depuis l'URL.
    //                 Ex : "/animal/42" → animalId = 42
    @GetMapping("/animal/{animalId}")
    public String listActesByAnimal(@PathVariable Long animalId, Model model) {
        Animal animal = animalService.findById(animalId);
        model.addAttribute("animal", animal);
        model.addAttribute("actes", acteMedicalService.findByAnimal(animalId));
        return "acte-medical/list";
    }

    // @GetMapping("/nouveau") : Affiche le formulaire de création d'un acte médical.
    // @PreAuthorize("hasRole('VETERINAIRE')") : SÉCURITÉ — Seul un utilisateur avec le rôle
    //                                            VETERINAIRE peut accéder à cette URL.
    //                                            Sinon Spring Security bloque l'accès (403 Forbidden).
    @GetMapping("/nouveau")
    @PreAuthorize("hasRole('VETERINAIRE')")
    // @RequestParam(required = false) : Lit un paramètre optionnel depuis l'URL.
    //                                    Ex : "/nouveau?animalId=5" → animalId = 5
    //                                    Si absent, animalId vaut null (car required = false).
    public String formNouvelActe(Model model,
                                  @RequestParam(required = false) Long animalId) {
        model.addAttribute("acte", new ActeMedical());
        model.addAttribute("animaux", animalService.findAll());
        model.addAttribute("typesSoin", TypeSoin.values());
        model.addAttribute("selectedAnimalId", animalId);
        return "acte-medical/form";
    }

    // @PostMapping("/nouveau") : Écoute les requêtes HTTP POST sur "/actes-medicaux/nouveau".
    //                             Utilisé pour SOUMETTRE un formulaire (création de données).
    @PostMapping("/nouveau")
    @PreAuthorize("hasRole('VETERINAIRE')")
    // @ModelAttribute : Lie automatiquement les données du formulaire HTML à l'objet ActeMedical.
    //                    Spring remplit tous les champs de l'objet depuis les inputs du formulaire.
    // Authentication : Objet Spring Security qui représente l'utilisateur actuellement connecté.
    //                   Permet de récupérer son email avec authentication.getName().
    // RedirectAttributes : Permet d'envoyer un message de succès/erreur APRÈS une redirection.
    //                        Les données flash sont disponibles une seule fois sur la page suivante.
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

    // @PostMapping("/supprimer/{id}") : Reçoit une requête POST pour supprimer un acte.
    //                                    On utilise POST (et non DELETE) car les formulaires
    //                                    HTML ne supportent que GET et POST nativement.
    @PostMapping("/supprimer/{id}")
    @PreAuthorize("hasRole('VETERINAIRE')")
    public String supprimerActe(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        acteMedicalService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Acte médical supprimé.");
        return "redirect:/actes-medicaux";
    }
}
