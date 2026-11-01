package com.bergerie.entity;

import com.bergerie.enums.StatutConnexion;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Liaison vétérinaire - bergerie. Un vétérinaire ne consulte une bergerie que si
 * son éleveur a accepté la connexion (statut ACCEPTEE).
 */
@Entity
@Table(name = "connexions_veterinaires",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_veterinaire", "id_bergerie"}))
@Getter
@Setter
@NoArgsConstructor
public class ConnexionVeterinaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_veterinaire", nullable = false)
    private Veterinaire veterinaire;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_bergerie", nullable = false)
    private Bergerie bergerie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutConnexion statut = StatutConnexion.EN_ATTENTE;

    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande = LocalDateTime.now();

    @Column(name = "date_reponse")
    private LocalDateTime dateReponse;
}
