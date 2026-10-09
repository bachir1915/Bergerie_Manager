package com.bergerie.entity;

import com.bergerie.enums.StatutAnimal;
import com.bergerie.enums.StatutSante;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// @Entity : Indique à JPA/Hibernate que cette classe Java représente une table en base de données.
//           Chaque objet Animal créé correspondra à une ligne dans la table.
@Entity

// @Table(name = "animaux") : Précise le nom exact de la table en base de données.
//                            Sans cela, JPA utiliserait le nom de la classe ("Animal").
@Table(name = "animaux")

// @Data (Lombok) : Génère automatiquement getters, setters, toString, equals, hashCode.
//                  Évite d'écrire des dizaines de lignes de code répétitif.
@Data

// @NoArgsConstructor (Lombok) : Génère un constructeur vide : new Animal()
//                               Obligatoire pour JPA qui en a besoin pour créer des objets.
@NoArgsConstructor

// @AllArgsConstructor (Lombok) : Génère un constructeur avec TOUS les champs en paramètres.
//                                Pratique pour créer un animal avec toutes ses données en une ligne.
@AllArgsConstructor

@EqualsAndHashCode(exclude = {"localisations", "alertes", "actesMedicaux", "capteurs"})
public class Animal {

    // @Id : Désigne ce champ comme clé primaire (PRIMARY KEY) de la table.
    @Id

    // @GeneratedValue(strategy = IDENTITY) : La valeur de l'ID est générée automatiquement
    //                                        par la base de données (auto-incrément : 1, 2, 3...).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false) : Ce champ est obligatoire en base de données.
    //                              Correspond à NOT NULL dans SQL.
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String race;

    @Column(nullable = false)
    private String sexe;

    private Double poids;

    // @Column(name = "date_naissance") : Le champ Java s'appelle "dateNaissance" mais
    //                                    en base de données la colonne s'appelle "date_naissance".
    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    // @Enumerated(EnumType.STRING) : Stocke la valeur de l'enum sous forme de texte ("ACTIF", "MALADE"...)
    //                                 au lieu d'un numéro (0, 1, 2...). Plus lisible en base de données.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAnimal statut = StatutAnimal.ACTIF;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_sante", nullable = false)
    private StatutSante statutSante = StatutSante.SAIN;

    // unique = true : Ce champ doit être unique pour chaque animal (comme une carte d'identité).
    //                  Correspond à UNIQUE en SQL.
    @Column(name = "numero_capteur", unique = true)
    private String numeroCapteur;

    // @ManyToOne : Plusieurs animaux peuvent appartenir à UNE seule bergerie (relation N:1).
    // fetch = LAZY : La bergerie n'est chargée depuis la DB QUE quand on y accède,
    //                pas automatiquement. Optimise les performances.
    @ManyToOne(fetch = FetchType.LAZY)

    // @JoinColumn(name = "id_bergerie") : Crée la clé étrangère "id_bergerie" dans la table animaux.
    @JoinColumn(name = "id_bergerie", nullable = false)
    private Bergerie bergerie;

    // @OneToMany : Un animal peut avoir PLUSIEURS localisations (relation 1:N).
    // mappedBy = "animal" : La relation est déjà gérée du côté de Localisation.java.
    // cascade = ALL : Toutes les opérations (save, delete...) se propagent aux localisations de l'animal.
    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Localisation> localisations = new ArrayList<>();

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alerte> alertes = new ArrayList<>();

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActeMedical> actesMedicaux = new ArrayList<>();

    @OneToMany(mappedBy = "animal", fetch = FetchType.LAZY)
    private List<CapteurAnimal> capteurs = new ArrayList<>();
}
