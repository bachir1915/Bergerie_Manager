package com.bergerie.controller.api;

import com.bergerie.dto.VeterinairePublicDto;
import com.bergerie.service.VeterinaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Annuaire des vétérinaires côté éleveur.
 * N'expose que les comptes VALIDE et uniquement les informations de contact,
 * jamais l'email de connexion, le mot de passe ou le justificatif.
 */
@RestController
@RequestMapping("/api/veterinaires")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ELEVEUR', 'VETERINAIRE', 'ADMIN')")
public class VeterinaireController {

    private final VeterinaireService veterinaireService;

    @GetMapping
    public List<VeterinairePublicDto> annuaire() {
        return veterinaireService.listerValides().stream()
                .map(VeterinairePublicDto::from)
                .toList();
    }
}
