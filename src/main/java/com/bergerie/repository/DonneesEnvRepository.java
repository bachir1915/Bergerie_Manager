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

    // Mesures de toutes les zones d'une bergerie donnée (espace vétérinaire).
    @Query("SELECT d FROM DonneesEnvironnementales d WHERE d.zone.bergerie.id = :bergerieId ORDER BY d.dateHeure DESC")
    List<DonneesEnvironnementales> findByBergerieId(Long bergerieId);

    @Query("SELECT d FROM DonneesEnvironnementales d WHERE d.zone.bergerie.id IN :bergerieIds ORDER BY d.dateHeure DESC")
    List<DonneesEnvironnementales> findByBergerieIds(List<Long> bergerieIds);

    @Query("SELECT d FROM DonneesEnvironnementales d WHERE d.zone.bergerie.id = :bergerieId AND d.dateHeure >= :after ORDER BY d.dateHeure DESC")
    List<DonneesEnvironnementales> findRecentByBergerieId(Long bergerieId, java.time.LocalDateTime after);
}
