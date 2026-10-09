package com.bergerie.controller;

import com.bergerie.entity.Zone;
import com.bergerie.entity.Eleveur;
import com.bergerie.repository.BergerieRepository;
import com.bergerie.repository.EleveurRepository;
import com.bergerie.service.ZoneService;
import lombok.RequiredArgsConstructor;
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

@Controller
@RequestMapping("/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;
    private final EleveurRepository eleveurRepository;
    private final BergerieRepository bergerieRepository;

    @GetMapping
    public String listZones(Model model, Authentication authentication) {
        String email = authentication.getName();
        boolean isEleveur = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ELEVEUR"));

        List<Zone> zones;
        if (isEleveur) {
            Eleveur eleveur = eleveurRepository.findByEmail(email).orElseThrow();
            if (!eleveur.getBergeries().isEmpty()) {
                zones = eleveur.getBergeries().stream()
                    .flatMap(b -> zoneService.findByBergerie(b.getId()).stream())
                    .toList();
            } else {
                zones = zoneService.findAll();
            }
        } else {
            zones = zoneService.findAll();
        }

        model.addAttribute("zones", zones);
        model.addAttribute("bergeries", bergerieRepository.findAll());
        model.addAttribute("zone", new Zone());
        return "zone/list";
    }

    @PostMapping("/nouveau")
    public String ajouterZone(@ModelAttribute Zone zone,
                              @RequestParam Long bergerieId,
                              RedirectAttributes redirectAttributes) {
        zone.setBergerie(bergerieRepository.findById(bergerieId).orElseThrow());
        zoneService.save(zone);
        redirectAttributes.addFlashAttribute("success", "Zone ajoutée avec succès !");
        return "redirect:/zones";
    }

    @PostMapping("/supprimer/{id}")
    public String supprimerZone(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        zoneService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Zone supprimée.");
        return "redirect:/zones";
    }
}
