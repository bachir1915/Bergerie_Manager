package com.bergerie.service;

import com.bergerie.entity.Utilisateur;
import com.bergerie.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
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
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())))
            .build();
    }
}
