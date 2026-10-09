package com.bergerie.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bergeries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"zones", "animaux", "eleveur"})
public class Bergerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    // Code d'identification unique de la bergerie, format AA-76-VI-77 :
    // 2 lettres majuscules - 2 chiffres - chiffre romain - 2 chiffres.
    // Un vétérinaire peut se connecter avec (email + ce code) pour consulter cette bergerie.
    @Column(name = "code", unique = true, length = 20)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    private String region;

    @Column(name = "superficie_totale")
    private Double superficieTotale;

    // Un éleveur peut posséder plusieurs bergeries.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_eleveur", nullable = false)
    private Eleveur eleveur;

    @OneToMany(mappedBy = "bergerie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Zone> zones = new ArrayList<>();

    @OneToMany(mappedBy = "bergerie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Animal> animaux = new ArrayList<>();
}
