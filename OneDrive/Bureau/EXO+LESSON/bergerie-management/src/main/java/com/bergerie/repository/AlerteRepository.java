package com.bergerie.repository;

import com.bergerie.entity.Alerte;
import com.bergerie.enums.StatutAlerte;
import com.bergerie.enums.TypeAlerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Long> {

    List<Alerte> findByUtilisateurDestIdOrderByDateHeureDesc(Long userId);

    List<Alerte> findByStatutAlerteOrderByDateHeureDesc(StatutAlerte statut);

    List<Alerte> findByAnimalIdOrderByDateHeureDesc(Long animalId);

    List<Alerte> findByTypeOrderByDateHeureDesc(TypeAlerte type);

    long countByStatutAlerte(StatutAlerte statut);

    long countByUtilisateurDestIdAndStatutAlerte(Long userId, StatutAlerte statut);

    List<Alerte> findTop10ByOrderByDateHeureDesc();
}
