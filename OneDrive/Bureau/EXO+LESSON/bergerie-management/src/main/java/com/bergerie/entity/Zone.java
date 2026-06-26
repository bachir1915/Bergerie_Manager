package com.bergerie.entity;

import com.bergerie.enums.TypeZone;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "zones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"capteurs", "donneesEnvironnementales"})
@ToString(exclude = {"capteurs", "donneesEnvironnementales", "bergerie"})
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_zone")
    private TypeZone typeZone;

    private Double latitude;
    private Double longitude;
    private Double superficie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bergerie", nullable = false)
    private Bergerie bergerie;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Capteur> capteurs = new ArrayList<>();

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonneesEnvironnementales> donneesEnvironnementales = new ArrayList<>();
}
