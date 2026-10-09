package com.bergerie.repository;

import com.bergerie.entity.Alerte;
import com.bergerie.enums.StatutAlerte;
import com.bergerie.enums.TypeAlerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Long> {

    @Query("SELECT DISTINCT a FROM Alerte a JOIN a.notifications n WHERE n.utilisateurDest.id = :userId ORDER BY a.dateHeure DESC")
    List<Alerte> findByUtilisateurDestIdOrderByDateHeureDesc(@Param("userId") Long userId);

    List<Alerte> findByStatutAlerteOrderByDateHeureDesc(StatutAlerte statut);

    List<Alerte> findByAnimalIdOrderByDateHeureDesc(Long animalId);

    List<Alerte> findByTypeOrderByDateHeureDesc(TypeAlerte type);

    long countByStatutAlerte(StatutAlerte statut);

    @Query("SELECT COUNT(DISTINCT a) FROM Alerte a JOIN a.notifications n WHERE n.utilisateurDest.id = :userId AND a.statutAlerte = :statut")
    long countByUtilisateurDestIdAndStatutAlerte(@Param("userId") Long userId, @Param("statut") StatutAlerte statut);

    List<Alerte> findTop10ByOrderByDateHeureDesc();

    @Query("SELECT a FROM Alerte a WHERE a.animal.bergerie.id = :bergerieId ORDER BY a.dateHeure DESC")
    List<Alerte> findByBergerieId(@Param("bergerieId") Long bergerieId);

    @Query("SELECT a FROM Alerte a WHERE a.animal.bergerie.id IN :bergerieIds ORDER BY a.dateHeure DESC")
    List<Alerte> findByBergerieIds(@Param("bergerieIds") List<Long> bergerieIds);
}
