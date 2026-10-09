package com.bergerie.entity;

import com.bergerie.enums.Role;
import com.bergerie.enums.StatutCompte;
import com.bergerie.enums.StatutValidation;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "veterinaires")
@DiscriminatorValue("VETERINAIRE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Veterinaire extends Utilisateur {

    // Spécialité affichée publiquement aux éleveurs (ex : "Ruminants", "Chirurgie").
    @Column(name = "specialite")
    private String specialite;

    // ---- Validation du compte par l'Admin ----

    // Statut de validation : EN_ATTENTE par défaut à l'inscription.
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_validation", nullable = false, length = 20)
    private StatutValidation statutValidation = StatutValidation.EN_ATTENTE;

    // Motif renseigné par l'Admin si le compte est REJETE ou SUSPENDU (sinon null).
    @Column(name = "motif_suspension", columnDefinition = "TEXT")
    private String motifSuspension;

    // ---- Justificatif d'inscription (carte pro, attestation à l'ordre, diplôme...) ----
    // Le fichier lui-même est stocké sur le disque ; on garde ici les métadonnées.

    @Column(name = "justificatif_nom_fichier")
    private String justificatifNomFichier;

    @Column(name = "justificatif_type_contenu")
    private String justificatifTypeContenu;

    // Chemin relatif du fichier dans le répertoire de stockage configuré.
    @Column(name = "justificatif_chemin", length = 500)
    private String justificatifChemin;

    /**
     * Constructeur utilisé par les données de démo.
     * Le vétérinaire se connecte avec (email + code de bergerie), pas avec un mot de passe :
     * {@code motDePasse} contient donc juste une valeur aléatoire inutilisable.
     */
    public Veterinaire(Long id, String nom, String prenom, String email,
                       String telephone, String motDePasse) {
        super(id, nom, prenom, email, telephone, motDePasse,
              Role.VETERINAIRE, StatutCompte.ACTIF,
              LocalDateTime.now(), null);
    }

    public boolean estValide() {
        return statutValidation == StatutValidation.VALIDE;
    }

    public boolean aUnJustificatif() {
        return justificatifChemin != null && !justificatifChemin.isBlank();
    }
}
