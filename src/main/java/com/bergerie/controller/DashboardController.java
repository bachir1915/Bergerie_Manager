package com.bergerie.controller;

import com.bergerie.entity.Bergerie;
import com.bergerie.entity.Eleveur;
import com.bergerie.entity.Utilisateur;
import com.bergerie.repository.EleveurRepository;
import com.bergerie.repository.UtilisateurRepository;
import com.bergerie.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UtilisateurRepository utilisateurRepository;
    private final EleveurRepository eleveurRepository;

    @GetMapping("/eleveur")
    public String dashboardEleveur(Model model, Authentication authentication) {
        String email = authentication.getName();
        Eleveur eleveur = eleveurRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Éleveur non trouvé"));

        List<Long> bergerieIds = eleveur.getBergeries().stream().map(Bergerie::getId).toList();
        Map<String, Object> stats = dashboardService.getDashboardEleveur(bergerieIds);

        model.addAttribute("eleveur", eleveur);
        model.addAttribute("stats", stats);
        model.addAllAttributes(stats);

        return "dashboard/eleveur";
    }

    @GetMapping("/veterinaire")
    public String dashboardVeterinaire(Model model, Authentication authentication) {
        String email = authentication.getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Map<String, Object> stats = dashboardService.getDashboardVeterinaire();

        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("stats", stats);
        model.addAllAttributes(stats);

        return "dashboard/veterinaire";
    }
}
