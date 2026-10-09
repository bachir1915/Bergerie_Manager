package com.bergerie.entity;

import com.bergerie.enums.NiveauAlerte;
import com.bergerie.enums.StatutAlerte;
import com.bergerie.enums.TypeAlerte;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alertes")
@Data
@NoArgsConstructor
@AllArgsConstructor
// Exclut les relations pour éviter les boucles infinies lors de la comparaison (equals/hashCode)
@EqualsAndHashCode(exclude = {"animal", "notifications"})
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_animal")
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeAlerte type;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauAlerte niveau = NiveauAlerte.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_alerte", nullable = false)
    private StatutAlerte statutAlerte = StatutAlerte.NOUVELLE;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure = LocalDateTime.now();

    @OneToMany(mappedBy = "alerte", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();
}
