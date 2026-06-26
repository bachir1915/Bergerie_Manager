package com.bergerie.entity;

import com.bergerie.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "eleveurs")
@DiscriminatorValue("ELEVEUR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "bergerie")
@ToString(callSuper = true, exclude = "bergerie")
public class Eleveur extends Utilisateur {

    @Column(name = "num_eleveur", unique = true)
    private String numEleveur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bergerie")
    private Bergerie bergerie;

    public Eleveur(Long id, String nom, String prenom, String email,
                   String telephone, String motDePasse, String numEleveur,
                   Bergerie bergerie) {
        super(id, nom, prenom, email, telephone, motDePasse,
              Role.ELEVEUR, com.bergerie.enums.StatutCompte.ACTIF,
              java.time.LocalDateTime.now());
        this.numEleveur = numEleveur;
        this.bergerie = bergerie;
    }
}
