package com.bergerie.entity;

import com.bergerie.enums.Role;
import com.bergerie.enums.StatutCompte;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entité Admin représentant un administrateur système.
 * Note d'architecture : Aucun attribut "role" spécifique n'est déclaré ici car il ferait
 * doublon avec le champ "role" (Role.ADMIN) et "statutCompte" hérités de Utilisateur.
 * Aucun sous-rôle admin n'étant requis par le modèle métier, l'héritage standard suffit.
 */
@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends Utilisateur {

    public Admin(Long id, String nom, String prenom, String email, String telephone, String motDePasse) {
        super(id, nom, prenom, email, telephone, motDePasse,
              Role.ADMIN, StatutCompte.ACTIF,
              LocalDateTime.now(), null);
    }
}
