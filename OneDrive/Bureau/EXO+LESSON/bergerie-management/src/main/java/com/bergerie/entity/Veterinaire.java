package com.bergerie.entity;

import com.bergerie.enums.Role;
import com.bergerie.enums.Specialite;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "veterinaires")
@DiscriminatorValue("VETERINAIRE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Veterinaire extends Utilisateur {

    @Column(name = "numero_ordre", unique = true)
    private String numeroOrdre;

    @Enumerated(EnumType.STRING)
    private Specialite specialite;

    public Veterinaire(Long id, String nom, String prenom, String email,
                       String telephone, String motDePasse,
                       String numeroOrdre, Specialite specialite) {
        super(id, nom, prenom, email, telephone, motDePasse,
              Role.VETERINAIRE, com.bergerie.enums.StatutCompte.ACTIF,
              java.time.LocalDateTime.now());
        this.numeroOrdre = numeroOrdre;
        this.specialite = specialite;
    }
}
