package com.bergerie.service;

import com.bergerie.entity.Utilisateur;
import com.bergerie.entity.Veterinaire;
import com.bergerie.enums.StatutValidation;
import com.bergerie.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        return User.builder()
            .username(utilisateur.getEmail())
            .password(utilisateur.getMotDePasse())
            .authorities(authorities(utilisateur))
            .build();
    }

    /**
     * Un vétérinaire dont le compte n'est pas VALIDE (EN_ATTENTE, REJETE, SUSPENDU) reçoit le rôle
     * dégradé {@code ROLE_VETERINAIRE_EN_ATTENTE} au lieu de {@code ROLE_VETERINAIRE}.
     * Toutes les règles de sécurité qui exigent {@code hasRole('VETERINAIRE')} le bloquent donc
     * automatiquement, même s'il est connecté, sans toucher au reste du code.
     */
    private List<SimpleGrantedAuthority> authorities(Utilisateur utilisateur) {
        if (utilisateur instanceof Veterinaire vet
                && vet.getStatutValidation() != StatutValidation.VALIDE) {
            return List.of(new SimpleGrantedAuthority("ROLE_VETERINAIRE_EN_ATTENTE"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name()));
    }
}
