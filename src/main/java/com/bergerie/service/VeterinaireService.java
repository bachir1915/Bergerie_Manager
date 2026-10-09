package com.bergerie.service;

import com.bergerie.entity.Veterinaire;
import com.bergerie.enums.Role;
import com.bergerie.enums.StatutCompte;
import com.bergerie.enums.StatutValidation;
import com.bergerie.repository.UtilisateurRepository;
import com.bergerie.repository.VeterinaireRepository;
import com.bergerie.util.Telephone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Logique métier autour du compte vétérinaire :
 * inscription (avec upload du justificatif) et cycle de vie du statut de validation.
 *
 * Un vétérinaire n'a pas de mot de passe : il se connecte avec (email + code de bergerie).
 */
@Service
@RequiredArgsConstructor
public class VeterinaireService {

    private final VeterinaireRepository veterinaireRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final JustificatifStorageService justificatifStorage;
    private final PasswordEncoder passwordEncoder;

    // ------------------------------------------------------------------
    // Inscription publique d'un vétérinaire (statut EN_ATTENTE)
    // ------------------------------------------------------------------

    @Transactional
    public Veterinaire inscrire(String nom, String prenom, String email, String telephone,
                                String specialite, MultipartFile justificatif) {

        if (utilisateurRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé.");
        }

        // Upload obligatoire : sans ce document l'Admin ne peut pas vérifier la qualité de vétérinaire.
        String chemin = justificatifStorage.stocker(justificatif);

        Veterinaire v = new Veterinaire();
        v.setNom(nom);
        v.setPrenom(prenom);
        v.setEmail(email);
        v.setTelephone(Telephone.normaliser(telephone));
        // Pas de mot de passe pour un vétérinaire : valeur aléatoire inutilisable.
        v.setMotDePasse(passwordEncoder.encode(UUID.randomUUID().toString()));
        v.setRole(Role.VETERINAIRE);
        v.setStatutCompte(StatutCompte.ACTIF);
        v.setDateCreation(LocalDateTime.now());
        v.setSpecialite(specialite);
        v.setStatutValidation(StatutValidation.EN_ATTENTE);
        v.setJustificatifChemin(chemin);
        v.setJustificatifNomFichier(justificatif.getOriginalFilename());
        v.setJustificatifTypeContenu(justificatif.getContentType());

        return veterinaireRepository.save(v);
    }

    // ------------------------------------------------------------------
    // Lecture
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Veterinaire> lister(StatutValidation filtre) {
        if (filtre == null) {
            return veterinaireRepository.findAll();
        }
        return veterinaireRepository.findByStatutValidationOrderByDateCreationDesc(filtre);
    }

    @Transactional(readOnly = true)
    public Veterinaire getOuErreur(Long id) {
        return veterinaireRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vétérinaire introuvable."));
    }

    /** Liste réservée aux éleveurs : uniquement les comptes VALIDE. */
    @Transactional(readOnly = true)
    public List<Veterinaire> listerValides() {
        return veterinaireRepository.findByStatutValidationOrderByNomAsc(StatutValidation.VALIDE);
    }

    @Transactional(readOnly = true)
    public Justificatif chargerJustificatif(Long id) {
        Veterinaire v = getOuErreur(id);
        if (!v.aUnJustificatif()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ce vétérinaire n'a pas fourni de justificatif.");
        }
        Resource resource = justificatifStorage.charger(v.getJustificatifChemin());
        String type = v.getJustificatifTypeContenu() != null
                ? v.getJustificatifTypeContenu() : "application/octet-stream";
        String nom = v.getJustificatifNomFichier() != null
                ? v.getJustificatifNomFichier() : "justificatif";
        return new Justificatif(resource, type, nom);
    }

    // ------------------------------------------------------------------
    // Transitions de statut (Admin)
    // ------------------------------------------------------------------

    /** EN_ATTENTE / REJETE / SUSPENDU -> VALIDE. */
    @Transactional
    public Veterinaire valider(Long id) {
        Veterinaire v = getOuErreur(id);
        v.setStatutValidation(StatutValidation.VALIDE);
        v.setMotifSuspension(null);
        return veterinaireRepository.save(v);
    }

    /** Rejet d'une inscription : -> REJETE, motif obligatoire. */
    @Transactional
    public Veterinaire rejeter(Long id, String motif) {
        exigerMotif(motif);
        Veterinaire v = getOuErreur(id);
        if (v.getStatutValidation() != StatutValidation.EN_ATTENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Seule une inscription EN_ATTENTE peut être rejetée. Utilisez la suspension pour un compte déjà validé.");
        }
        v.setStatutValidation(StatutValidation.REJETE);
        v.setMotifSuspension(motif);
        return veterinaireRepository.save(v);
    }

    /** Suspension d'un compte déjà validé, à tout moment : -> SUSPENDU, motif obligatoire. */
    @Transactional
    public Veterinaire suspendre(Long id, String motif) {
        exigerMotif(motif);
        Veterinaire v = getOuErreur(id);
        if (v.getStatutValidation() != StatutValidation.VALIDE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Seul un compte VALIDE peut être suspendu.");
        }
        v.setStatutValidation(StatutValidation.SUSPENDU);
        v.setMotifSuspension(motif);
        return veterinaireRepository.save(v);
    }

    /** Réactivation d'un compte suspendu : SUSPENDU -> VALIDE. */
    @Transactional
    public Veterinaire reactiver(Long id) {
        Veterinaire v = getOuErreur(id);
        if (v.getStatutValidation() != StatutValidation.SUSPENDU) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Seul un compte SUSPENDU peut être réactivé.");
        }
        v.setStatutValidation(StatutValidation.VALIDE);
        v.setMotifSuspension(null);
        return veterinaireRepository.save(v);
    }

    private void exigerMotif(String motif) {
        if (motif == null || motif.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le motif est obligatoire.");
        }
    }

    /** Fichier justificatif prêt à être renvoyé par le contrôleur. */
    public record Justificatif(Resource resource, String typeContenu, String nomFichier) {
    }
}
