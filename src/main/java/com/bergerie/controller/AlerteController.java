package com.bergerie.controller;

import com.bergerie.entity.Alerte;
import com.bergerie.entity.Utilisateur;
import com.bergerie.repository.UtilisateurRepository;
import com.bergerie.service.AlerteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/alertes")
@RequiredArgsConstructor
public class AlerteController {

    private final AlerteService alerteService;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping
    public String listAlertes(Model model, Authentication authentication) {
        String email = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElseThrow();

        List<Alerte> alertes = alerteService.findByUtilisateur(utilisateur.getId());
        long nonLues = alerteService.countNonLuesPourUtilisateur(utilisateur.getId());

        model.addAttribute("alertes", alertes);
        model.addAttribute("nonLues", nonLues);
        return "alerte/list";
    }

    @PostMapping("/marquer-lue/{id}")
    public String marquerLue(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        alerteService.marquerLue(id);
        redirectAttributes.addFlashAttribute("success", "Alerte marquée comme lue.");
        return "redirect:/alertes";
    }

    @PostMapping("/marquer-traitee/{id}")
    public String marquerTraitee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        alerteService.marquerTraitee(id);
        redirectAttributes.addFlashAttribute("success", "Alerte marquée comme traitée.");
        return "redirect:/alertes";
    }
}
