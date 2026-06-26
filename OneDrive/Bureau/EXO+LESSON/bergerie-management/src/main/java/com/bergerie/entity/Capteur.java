package com.bergerie.entity;

import com.bergerie.enums.StatutCapteur;
import com.bergerie.enums.TypeCapteur;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "capteurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"animal", "zone"})
@ToString(exclude = {"animal", "zone"})
public class Capteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeCapteur type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCapteur statut = StatutCapteur.ACTIF;

    @Column(name = "date_installation")
    private LocalDate dateInstallation;

    @Column(name = "derniere_synchro")
    private LocalDateTime derniereSynchro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_animal")
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zone")
    private Zone zone;
}
