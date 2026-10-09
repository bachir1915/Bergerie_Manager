package com.bergerie.repository;

import com.bergerie.entity.CapteurZone;
import com.bergerie.enums.StatutCapteur;
import com.bergerie.enums.TypeCapteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapteurZoneRepository extends JpaRepository<CapteurZone, Long> {
    List<CapteurZone> findByZoneId(Long zoneId);
    Optional<CapteurZone> findByZoneIdAndType(Long zoneId, TypeCapteur type);
    List<CapteurZone> findByType(TypeCapteur type);
    List<CapteurZone> findByTypeAndStatut(TypeCapteur type, StatutCapteur statut);
}
