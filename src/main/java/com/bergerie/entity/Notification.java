package com.bergerie.entity;

import com.bergerie.enums.CanalNotif;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"alerte", "utilisateurDest"})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alerte", nullable = false)
    private Alerte alerte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur_dest", nullable = false)
    private Utilisateur utilisateurDest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalNotif canal = CanalNotif.PUSH;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    public Notification(Alerte alerte, Utilisateur utilisateurDest, CanalNotif canal) {
        this.alerte = alerte;
        this.utilisateurDest = utilisateurDest;
        this.canal = canal;
        this.dateEnvoi = LocalDateTime.now();
        this.dateLecture = null;
    }
}
