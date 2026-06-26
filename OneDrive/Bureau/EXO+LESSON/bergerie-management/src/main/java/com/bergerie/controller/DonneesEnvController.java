package com.bergerie.controller;

import com.bergerie.service.DonneesEnvService;
import com.bergerie.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/donnees-env")
@RequiredArgsConstructor
public class DonneesEnvController {

    private final DonneesEnvService donneesEnvService;
    private final ZoneRepository zoneRepository;

    @GetMapping
    public String listDonnees(Model model) {
        model.addAttribute("donnees", donneesEnvService.findRecentes());
        model.addAttribute("zones", zoneRepository.findAll());
        return "donnees-env/list";
    }

    @GetMapping("/zone/{zoneId}")
    public String donneesByZone(@PathVariable Long zoneId, Model model) {
        model.addAttribute("donnees", donneesEnvService.findByZone(zoneId));
        model.addAttribute("zone", zoneRepository.findById(zoneId).orElseThrow());
        model.addAttribute("zones", zoneRepository.findAll());
        return "donnees-env/list";
    }
}
