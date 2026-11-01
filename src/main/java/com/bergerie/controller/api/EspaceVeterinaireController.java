package com.bergerie.controller.api;

import com.bergerie.dto.ActeForm;
import com.bergerie.dto.EspaceVeterinaireResponse;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.Veterinaire;
import com.bergerie.repository.VeterinaireRepository;
import com.bergerie.service.ConnexionService;
import com.bergerie.service.EspaceVeterinaireService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Espace vétérinaire, focalisé sur une bergerie identifiée par son code.
 * Accessible uniquement à un vétérinaire VALIDE (rôle VETERINAIRE) dont la connexion
 * à la bergerie a été acceptée par l'éleveur.
 */
@RestController
@RequestMapping("/api/veterinaire")
@RequiredArgsConstructor
@PreAuthorize("hasRole('VETERINAIRE')")
public class EspaceVeterinaireController {

    private final EspaceVeterinaireService espaceService;
    private final ConnexionService connexionService;
    private final VeterinaireRepository veterinaireRepository;

    /** Infos de la bergerie couramment consultée (choisie à la connexion ou via POST /bergerie). */
    @GetMapping("/espace")
    public EspaceVeterinaireResponse espace(Authentication auth, HttpSession session) {
        return espaceService.construire(bergerieCouranteOuErreur(auth, session));
    }

    /** Change (ou définit) la bergerie consultée à partir de son code AA-76-VI-77. */
    @PostMapping("/bergerie")
    public EspaceVeterinaireResponse choisirBergerie(@RequestParam String code, Authentication auth,
                                                     HttpSession session) {
        Bergerie bergerie = espaceService.parCodeOuErreur(code);
        Veterinaire vet = veterinaireRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vétérinaire inconnu."));
        connexionService.exigerAcceptee(vet, bergerie);
        session.setAttribute(EspaceVeterinaireService.SESSION_BERGERIE, bergerie.getId());
        return espaceService.construire(bergerie.getId());
    }

    /** Enregistre un acte médical pour un animal de la bergerie consultée. */
    @PostMapping("/actes")
    public EspaceVeterinaireResponse ajouterActe(@Valid @RequestBody ActeForm form,
                                                 Authentication auth,
                                                 HttpSession session) {
        return espaceService.ajouterActe(bergerieCouranteOuErreur(auth, session), auth.getName(), form);
    }

    /** Modifie un acte médical de la bergerie consultée. */
    @PutMapping("/actes/{id}")
    public EspaceVeterinaireResponse modifierActe(@PathVariable Long id, @Valid @RequestBody ActeForm form,
                                                  Authentication auth, HttpSession session) {
        return espaceService.modifierActe(bergerieCouranteOuErreur(auth, session), id, form);
    }

    /** Supprime un acte médical de la bergerie consultée. */
    @DeleteMapping("/actes/{id}")
    public EspaceVeterinaireResponse supprimerActe(@PathVariable Long id, Authentication auth, HttpSession session) {
        return espaceService.supprimerActe(bergerieCouranteOuErreur(auth, session), id);
    }

    private Long bergerieCouranteOuErreur(Authentication auth, HttpSession session) {
        Object id = session.getAttribute(EspaceVeterinaireService.SESSION_BERGERIE);
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Aucune bergerie sélectionnée. Connectez-vous avec un code de bergerie ou appelez POST /api/veterinaire/bergerie.");
        }
        Long bergerieId = (Long) id;
        if (!connexionService.estAcceptee(auth.getName(), bergerieId)) {
            session.removeAttribute(EspaceVeterinaireService.SESSION_BERGERIE);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "L'éleveur a annulé votre connexion à cette bergerie.");
        }
        return bergerieId;
    }
}
