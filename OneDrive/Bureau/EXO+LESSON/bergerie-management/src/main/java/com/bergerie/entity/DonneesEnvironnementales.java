package com.bergerie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donnees_environnementales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"zone", "capteur"})
@ToString(exclude = {"zone", "capteur"})
public class DonneesEnvironnementales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zone", nullable = false)
    private Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_capteur", nullable = false)
    private Capteur capteur;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double humidite;

    @Column(name = "qualite_air")
    private Double qualiteAir;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure = LocalDateTime.now();
}
