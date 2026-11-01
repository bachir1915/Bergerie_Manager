package com.bergerie.controller.api;

import com.bergerie.dto.ConnexionDto;
import com.bergerie.service.ConnexionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Connexions des vétérinaires à mes bergeries : l'éleveur vérifie, accepte, refuse ou annule. */
@RestController
@RequestMapping("/api/connexions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ELEVEUR')")
public class ConnexionController {

    private final ConnexionService connexionService;

    @GetMapping
    public List<ConnexionDto> lister(Authentication auth) {
        return connexionService.lister(auth.getName());
    }

    @PostMapping("/{id}/accepter")
    public ConnexionDto accepter(@PathVariable Long id, Authentication auth) {
        return connexionService.accepter(id, auth.getName());
    }

    @PostMapping("/{id}/refuser")
    public ConnexionDto refuser(@PathVariable Long id, Authentication auth) {
        return connexionService.refuser(id, auth.getName());
    }

    @PostMapping("/{id}/annuler")
    public ConnexionDto annuler(@PathVariable Long id, Authentication auth) {
        return connexionService.annuler(id, auth.getName());
    }
}
