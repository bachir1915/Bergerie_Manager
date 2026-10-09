package com.bergerie.entity;

import com.bergerie.enums.Role;
import com.bergerie.enums.StatutCompte;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eleveurs")
@DiscriminatorValue("ELEVEUR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "bergeries")
public class Eleveur extends Utilisateur {

    @Column(name = "num_eleveur", unique = true)
    private String numEleveur;

    // Un éleveur peut posséder plusieurs bergeries.
    @OneToMany(mappedBy = "eleveur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bergerie> bergeries = new ArrayList<>();

    public Eleveur(Long id, String nom, String prenom, String email,
                   String telephone, String motDePasse, String numEleveur) {
        super(id, nom, prenom, email, telephone, motDePasse,
              Role.ELEVEUR, StatutCompte.ACTIF,
              LocalDateTime.now(), null);
        this.numEleveur = numEleveur;
    }
}
