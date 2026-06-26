package com.bergerie.repository;

import com.bergerie.entity.Bergerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BergerieRepository extends JpaRepository<Bergerie, Long> {
}
