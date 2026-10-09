package com.bergerie.entity;

import com.bergerie.enums.TypeSoin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "actes_medicaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
// Exclut les relations bidirectionnelles pour éviter les boucles infinies lors de la comparaison (equals/hashCode)
@EqualsAndHashCode(exclude = {"animal", "utilisateur"})
public class ActeMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_animal", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_soin", nullable = false)
    private TypeSoin typeSoin;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String medicament;

    private String dose;

    @Column(name = "date_soin", nullable = false)
    private LocalDate dateSoin;

    @Column(name = "prochain_rappel")
    private LocalDate prochainRappel;
}
