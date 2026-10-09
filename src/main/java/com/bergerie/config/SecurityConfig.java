package com.bergerie.config;

import com.bergerie.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// @Configuration : Indique que cette classe contient des configurations Spring.
//                   Elle remplace un fichier XML de configuration Spring traditionnel.
//                   Spring la charge au démarrage de l'application.
@Configuration

// @EnableWebSecurity : Active le module de sécurité Web de Spring Security.
//                       Sans cette annotation, toutes les URLs seraient accessibles sans authentification.
@EnableWebSecurity

// @EnableMethodSecurity : Active les annotations @PreAuthorize / @PostAuthorize sur les méthodes
//                          (ex : @PreAuthorize("hasRole('ADMIN')") sur AdminController).
@EnableMethodSecurity

// @RequiredArgsConstructor (Lombok) : Génère le constructeur avec le champ "final" userDetailsService.
//                                      Spring l'injecte automatiquement au démarrage.
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    // @Bean : Indique à Spring de gérer cet objet dans son conteneur (IoC Container).
    //          Spring crée une seule instance de PasswordEncoder et la réutilise partout.
    //          Ici, BCryptPasswordEncoder est utilisé pour hacher les mots de passe.
    //          BCrypt est un algorithme sécurisé : même le même mot de passe donne un hash différent.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // @Bean : Spring gère l'instance de AuthenticationManager.
    //          Il est responsable de vérifier si les identifiants (email + mot de passe) sont corrects.
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // CORS : autorise le front Angular (port 4200, différent de celui de Spring Boot) à appeler l'API.
    // allowCredentials(true) : indispensable pour que le navigateur envoie le cookie de session Angular -> Spring Boot.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    // Chaîne dédiée à l'API REST (front Angular) : authentification par session/cookie,
    // exactement comme pour le front Thymeleaf (pas de JWT) — le login crée une session,
    // le cookie de session est renvoyé automatiquement par le navigateur à chaque requête.
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/iot/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Un vétérinaire non VALIDE porte le rôle dégradé VETERINAIRE_EN_ATTENTE :
                // il n'a accès à AUCUNE fonctionnalité métier de l'API, même connecté.
                .anyRequest().hasAnyRole("ELEVEUR", "VETERINAIRE", "ADMIN")
            )
            // Sans session valide : répondre 401 (JSON) au lieu de rediriger vers /login (page HTML).
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) ->
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
            );

        return http.build();
    }

    // @Bean : Définit la chaîne de filtres de sécurité (SecurityFilterChain).
    //          C'est ici que sont configurées toutes les règles d'accès aux URLs de l'ancien front Thymeleaf.
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Swagger UI / OpenAPI 3 : accessible sans authentification pour la démo et le jury
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // permitAll() : Ces URLs sont accessibles par TOUT LE MONDE, même sans être connecté.
                .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**",
                                 "/h2-console/**").permitAll()
                // hasAnyRole(...) : Ces URLs nécessitent d'être connecté en tant qu'ELEVEUR OU VETERINAIRE.
                // Règles spécifiques d'abord (la première correspondance l'emporte).
                // hasRole("VETERINAIRE") : Seul un VETERINAIRE VALIDE peut créer/modifier/supprimer un acte médical.
                .requestMatchers("/actes-medicaux/nouveau", "/actes-medicaux/modifier/**",
                                 "/actes-medicaux/supprimer/**").hasRole("VETERINAIRE")
                // hasAnyRole(...) : connecté en tant qu'ELEVEUR OU VETERINAIRE (compte VALIDE).
                // Un vétérinaire non VALIDE porte le rôle VETERINAIRE_EN_ATTENTE et est donc exclu
                // de toutes ces fonctionnalités métier sensibles, même connecté.
                .requestMatchers("/dashboard/eleveur/**", "/animaux/**",
                                 "/alertes/**", "/donnees-env/**",
                                 "/localisation/**", "/zones/**",
                                 "/actes-medicaux/**").hasAnyRole("ELEVEUR", "VETERINAIRE")
                // anyRequest().authenticated() : Toute autre URL nécessite d'être connecté.
                .anyRequest().authenticated()
            )
            // formLogin(...) : Configure le formulaire de connexion personnalisé.
            .formLogin(form -> form
                .loginPage("/login")             // URL de la page de connexion personnalisée.
                .loginProcessingUrl("/login")     // URL qui traite le formulaire POST de connexion.
                .successHandler((request, response, authentication) -> {
                    // Redirection selon le rôle après connexion réussie.
                    boolean isEleveur = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ELEVEUR"));
                    if (isEleveur) {
                        response.sendRedirect("/dashboard/eleveur");
                    } else {
                        response.sendRedirect("/dashboard/veterinaire");
                    }
                })
                .failureUrl("/login?error=true") // Redirige ici si le mot de passe est incorrect.
                .permitAll()
            )
            // logout(...) : Configure la déconnexion.
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true") // Redirige vers la page de login après déconnexion.
                .invalidateHttpSession(true)            // Détruit la session de l'utilisateur.
                .clearAuthentication(true)              // Efface les informations d'authentification.
                .permitAll()
            )
            // csrf(...) : Protection contre les attaques CSRF (Cross-Site Request Forgery).
            //              On désactive cette protection uniquement pour la console H2
            //              (les routes /api/** sont gérées par apiFilterChain, stateless donc sans CSRF).
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            // headers(...) : Permet d'afficher la console H2 dans un iframe (sameOrigin).
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
