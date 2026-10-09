package com.bergerie.controller.api;

import com.bergerie.dto.LoginRequest;
import com.bergerie.dto.UserInfoResponse;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.Utilisateur;
import com.bergerie.entity.Veterinaire;
import com.bergerie.enums.StatutValidation;
import com.bergerie.repository.UtilisateurRepository;
import com.bergerie.service.CodeConnexionService;
import com.bergerie.service.EspaceVeterinaireService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Connexion unifiée : <b>email + code</b> (aucun mot de passe).
 * Le code attendu dépend du rôle de l'utilisateur identifié par l'email :
 *   - ADMIN       -> son code personnel        (ADM-XXXX-XXXX)
 *   - ELEVEUR     -> son code personnel        (ELV-000000)
 *   - VETERINAIRE -> le code d'une bergerie    (AA-76-VI-77) ; le compte doit être VALIDE.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final UtilisateurRepository utilisateurRepository;
    private final EspaceVeterinaireService espaceVeterinaireService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/login")
    public ResponseEntity<UserInfoResponse> login(@RequestBody LoginRequest request,
                                                   HttpServletRequest httpRequest,
                                                   HttpServletResponse httpResponse) {
        String email = request.email() == null ? "" : request.email().trim();
        String code = CodeConnexionService.normaliser(request.code() == null ? "" : request.code());

        Utilisateur u = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou code incorrect."));

        Long bergerieId = null;
        List<SimpleGrantedAuthority> authorities;

        switch (u.getRole()) {
            case VETERINAIRE -> {
                Veterinaire vet = (Veterinaire) u;
                if (vet.getStatutValidation() != StatutValidation.VALIDE) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Votre compte vétérinaire n'est pas encore validé par l'administrateur.");
                }
                Bergerie bergerie = espaceVeterinaireService.parCodeOuErreur(code);
                bergerieId = bergerie.getId();
                authorities = List.of(new SimpleGrantedAuthority("ROLE_VETERINAIRE"));
            }
            case ELEVEUR, ADMIN -> {
                if (u.getCodeConnexion() == null || !u.getCodeConnexion().equals(code)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou code incorrect.");
                }
                authorities = List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()));
            }
            default -> throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou code incorrect.");
        }

        Authentication authResult =
            UsernamePasswordAuthenticationToken.authenticated(u.getEmail(), null, authorities);
        persisterSession(authResult, httpRequest, httpResponse);
        if (bergerieId != null) {
            httpRequest.getSession(true)
                .setAttribute(EspaceVeterinaireService.SESSION_BERGERIE, bergerieId);
        }

        return ResponseEntity.ok(toUserInfo(u.getEmail()));
    }

    @PostMapping("/demo")
    public ResponseEntity<UserInfoResponse> loginDemo(HttpServletRequest httpRequest,
                                                      HttpServletResponse httpResponse) {
        Utilisateur u = utilisateurRepository.findByEmail("eleveur@bergerie.com")
            .or(() -> utilisateurRepository.findAll().stream()
                .filter(usr -> usr.getRole() == com.bergerie.enums.Role.ELEVEUR)
                .findFirst())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun compte démo disponible."));

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ELEVEUR"));
        Authentication authResult =
            UsernamePasswordAuthenticationToken.authenticated(u.getEmail(), null, authorities);
        persisterSession(authResult, httpRequest, httpResponse);

        return ResponseEntity.ok(toUserInfo(u.getEmail()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> me(Authentication authentication) {
        return ResponseEntity.ok(toUserInfo(authentication.getName()));
    }

    private void persisterSession(Authentication authResult,
                                  HttpServletRequest httpRequest,
                                  HttpServletResponse httpResponse) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);
    }

    private UserInfoResponse toUserInfo(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new BadCredentialsException("Utilisateur non trouvé"));

        String statutValidation = (utilisateur instanceof Veterinaire vet)
            ? vet.getStatutValidation().name() : null;

        return new UserInfoResponse(
            utilisateur.getId(),
            utilisateur.getRole().name(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getEmail(),
            statutValidation
        );
    }
}
