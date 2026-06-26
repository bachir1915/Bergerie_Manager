package com.bergerie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bergeries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"zones", "animaux"})
@ToString(exclude = {"zones", "animaux"})
public class Bergerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    private String region;

    @Column(name = "superficie_totale")
    private Double superficieTotale;

    @OneToMany(mappedBy = "bergerie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Zone> zones = new ArrayList<>();

    @OneToMany(mappedBy = "bergerie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Animal> animaux = new ArrayList<>();
}
