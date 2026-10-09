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
@Table(name = "capteurs_zones")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "zone")
public class CapteurZone extends Capteur {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zone", nullable = false)
    private Zone zone;

    public CapteurZone(Long id, TypeCapteur type, StatutCapteur statut, LocalDate dateInstallation, Zone zone) {
        super(id, type, statut, dateInstallation);
        this.zone = zone;
    }
}
