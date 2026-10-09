package com.bergerie.controller.api;

import com.bergerie.dto.ActeForm;
import com.bergerie.dto.EspaceVeterinaireResponse;
import com.bergerie.entity.Bergerie;
import com.bergerie.service.EspaceVeterinaireService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Espace vétérinaire, focalisé sur une bergerie identifiée par son code.
 * Accessible uniquement à un vétérinaire VALIDE (rôle VETERINAIRE).
 */
@RestController
@RequestMapping("/api/veterinaire")
@RequiredArgsConstructor
@PreAuthorize("hasRole('VETERINAIRE')")
public class EspaceVeterinaireController {

    private final EspaceVeterinaireService espaceService;

    /** Infos de la bergerie couramment consultée (choisie à la connexion ou via POST /bergerie). */
    @GetMapping("/espace")
    public EspaceVeterinaireResponse espace(HttpSession session) {
        return espaceService.construire(bergerieCouranteOuErreur(session));
    }

    /** Change (ou définit) la bergerie consultée à partir de son code AA-76-VI-77. */
    @PostMapping("/bergerie")
    public EspaceVeterinaireResponse choisirBergerie(@RequestParam String code, HttpSession session) {
        Bergerie bergerie = espaceService.parCodeOuErreur(code);
        session.setAttribute(EspaceVeterinaireService.SESSION_BERGERIE, bergerie.getId());
        return espaceService.construire(bergerie.getId());
    }

    /** Enregistre un acte médical pour un animal de la bergerie consultée. */
    @PostMapping("/actes")
    public EspaceVeterinaireResponse ajouterActe(@Valid @RequestBody ActeForm form,
                                                 Authentication auth,
                                                 HttpSession session) {
        return espaceService.ajouterActe(bergerieCouranteOuErreur(session), auth.getName(), form);
    }

    private Long bergerieCouranteOuErreur(HttpSession session) {
        Object id = session.getAttribute(EspaceVeterinaireService.SESSION_BERGERIE);
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Aucune bergerie sélectionnée. Connectez-vous avec un code de bergerie ou appelez POST /api/veterinaire/bergerie.");
        }
        return (Long) id;
    }
}
