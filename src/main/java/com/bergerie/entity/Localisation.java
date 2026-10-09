package com.bergerie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.time.LocalDateTime;

@Entity
@Table(name = "localisations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"animal", "capteur"})
public class Localisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_animal", nullable = false)
    private Animal animal;

    // Capteur ayant enregistré cette position.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_capteur")
    private Capteur capteur;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure = LocalDateTime.now();

    private Double vitesse;

    @Column(name = "precision_gps")
    private Double precisionGps;
}
