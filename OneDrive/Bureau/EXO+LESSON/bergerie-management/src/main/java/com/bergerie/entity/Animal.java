package com.bergerie.entity;

import com.bergerie.enums.StatutAnimal;
import com.bergerie.enums.StatutSante;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "animaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"localisations", "alertes", "actesMedicaux", "capteurs"})
@ToString(exclude = {"localisations", "alertes", "actesMedicaux", "capteurs", "bergerie"})
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String race;

    @Column(nullable = false)
    private String sexe;

    private Double poids;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAnimal statut = StatutAnimal.ACTIF;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_sante", nullable = false)
    private StatutSante statutSante = StatutSante.BON;

    @Column(name = "numero_puce", unique = true)
    private String numeroPuce;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bergerie", nullable = false)
    private Bergerie bergerie;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Localisation> localisations = new ArrayList<>();

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alerte> alertes = new ArrayList<>();

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActeMedical> actesMedicaux = new ArrayList<>();

    @OneToMany(mappedBy = "animal", fetch = FetchType.LAZY)
    private List<Capteur> capteurs = new ArrayList<>();
}
