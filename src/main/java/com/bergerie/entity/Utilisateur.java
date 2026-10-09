package com.bergerie.entity;

import com.bergerie.enums.Role;
import com.bergerie.enums.StatutCompte;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// @Entity : Indique à JPA que cette classe abstraite est une entité persistante en base de données.
@Entity

// @Table(name = "utilisateurs") : Le nom de la table en base de données est "utilisateurs".
@Table(name = "utilisateurs")

// @Inheritance(strategy = JOINED) : Stratégie d'héritage "JOINED".
//   Il y a UNE table principale "utilisateurs" pour les champs communs,
//   ET des tables séparées "eleveurs" et "veterinaires" pour les champs spécifiques.
//   Les tables sont JOINÉES (liées) par la clé primaire.
@Inheritance(strategy = InheritanceType.JOINED)

// @DiscriminatorColumn : Ajoute une colonne "dtype" dans la table "utilisateurs"
//                         qui contient le type réel de l'objet ("Eleveur" ou "Veterinaire").
//                         Permet à Hibernate de savoir quelle sous-classe instancier.
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)

// @Data (Lombok) : Génère automatiquement getters, setters, equals, hashCode et toString.
@Data

// @NoArgsConstructor (Lombok) : Génère le constructeur vide, requis par JPA.
@NoArgsConstructor

// @AllArgsConstructor (Lombok) : Génère le constructeur avec tous les paramètres.
@AllArgsConstructor
public abstract class Utilisateur {

    // @Id : Ce champ est la clé primaire de la table.
    @Id

    // @GeneratedValue(strategy = IDENTITY) : L'ID est auto-incrémenté par la base de données.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    // unique = true : L'email doit être unique (pas deux utilisateurs avec le même email).
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String telephone;

    // @Column(name = "mot_de_passe") : En Java le champ s'appelle "motDePasse" (camelCase)
    //                                   mais en SQL la colonne s'appelle "mot_de_passe" (snake_case).
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    // @Enumerated(EnumType.STRING) : Stocke le rôle sous forme de texte ("ELEVEUR", "VETERINAIRE")
    //                                 et non pas comme un entier (0, 1).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_compte", nullable = false)
    private StatutCompte statutCompte = StatutCompte.ACTIF;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Code personnel de connexion (au lieu d'un mot de passe).
    //  - ADMIN   : format ADM-XXXX-XXXX
    //  - ELEVEUR : format ELV-000000
    //  - VETERINAIRE : null — le vétérinaire se connecte avec le CODE D'UNE BERGERIE.
    @Column(name = "code_connexion", unique = true, length = 20)
    private String codeConnexion;
}
