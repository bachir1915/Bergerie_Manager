package com.bergerie.entity;

import com.bergerie.enums.NiveauAlerte;
import com.bergerie.enums.StatutAlerte;
import com.bergerie.enums.TypeAlerte;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"animal", "capteur", "utilisateurDest"})
@ToString(exclude = {"animal", "capteur", "utilisateurDest"})
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_animal")
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_capteur")
    private Capteur capteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur_dest")
    private Utilisateur utilisateurDest;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeAlerte type;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauAlerte niveau = NiveauAlerte.INFO;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_alerte", nullable = false)
    private StatutAlerte statutAlerte = StatutAlerte.NON_LUE;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure = LocalDateTime.now();
}
