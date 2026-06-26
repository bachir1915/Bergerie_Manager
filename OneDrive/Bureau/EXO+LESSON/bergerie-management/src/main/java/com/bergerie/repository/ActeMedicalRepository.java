package com.bergerie.repository;

import com.bergerie.entity.ActeMedical;
import com.bergerie.enums.TypeSoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActeMedicalRepository extends JpaRepository<ActeMedical, Long> {

    List<ActeMedical> findByAnimalIdOrderByDateSoinDesc(Long animalId);

    List<ActeMedical> findByUtilisateurIdOrderByDateSoinDesc(Long userId);

    List<ActeMedical> findByTypeSoin(TypeSoin typeSoin);

    List<ActeMedical> findByProchainRappelBetween(LocalDate start, LocalDate end);

    long countByTypeSoin(TypeSoin typeSoin);
}
