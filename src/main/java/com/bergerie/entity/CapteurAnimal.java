package com.bergerie.entity;

import com.bergerie.enums.StatutCapteur;
import com.bergerie.enums.TypeCapteur;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "capteurs_animaux")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "animal")
public class CapteurAnimal extends Capteur {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_animal")
    private Animal animal;

    public CapteurAnimal(Long id, TypeCapteur type, StatutCapteur statut, LocalDate dateInstallation, Animal animal) {
        super(id, type, statut, dateInstallation);
        this.animal = animal;
    }
}
