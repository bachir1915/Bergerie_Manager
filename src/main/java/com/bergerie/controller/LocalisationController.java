package com.bergerie.controller;

import com.bergerie.service.LocalisationService;
import com.bergerie.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/localisation")
@RequiredArgsConstructor
public class LocalisationController {

    private final LocalisationService localisationService;
    private final AnimalService animalService;

    @GetMapping
    public String cartePage(Model model) {
        model.addAttribute("animaux", animalService.findAll());
        model.addAttribute("dernieresPositions", localisationService.findDernierePositionTousAnimaux());
        return "localisation/map";
    }

    @GetMapping("/animal/{animalId}")
    public String historyAnimal(@PathVariable Long animalId, Model model) {
        model.addAttribute("animal", animalService.findById(animalId));
        model.addAttribute("localisations", localisationService.findByAnimal(animalId));
        return "localisation/map";
    }
}
