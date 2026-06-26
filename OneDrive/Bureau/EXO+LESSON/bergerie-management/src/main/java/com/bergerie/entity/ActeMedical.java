package com.bergerie.entity;

import com.bergerie.enums.TypeSoin;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "actes_medicaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"animal", "utilisateur"})
@ToString(exclude = {"animal", "utilisateur"})
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
