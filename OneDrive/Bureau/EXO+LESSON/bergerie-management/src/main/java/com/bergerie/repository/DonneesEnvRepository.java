package com.bergerie.repository;

import com.bergerie.entity.DonneesEnvironnementales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonneesEnvRepository extends JpaRepository<DonneesEnvironnementales, Long> {

    List<DonneesEnvironnementales> findByZoneIdOrderByDateHeureDesc(Long zoneId);

    List<DonneesEnvironnementales> findTop10ByOrderByDateHeureDesc();

    @Query("SELECT AVG(d.temperature) FROM DonneesEnvironnementales d WHERE d.zone.id = :zoneId")
    Double avgTemperatureByZone(Long zoneId);

    @Query("SELECT AVG(d.humidite) FROM DonneesEnvironnementales d WHERE d.zone.id = :zoneId")
    Double avgHumiditeByZone(Long zoneId);
}
