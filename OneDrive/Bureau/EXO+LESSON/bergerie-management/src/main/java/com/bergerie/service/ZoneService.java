package com.bergerie.service;

import com.bergerie.entity.Zone;
import com.bergerie.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public List<Zone> findAll() {
        return zoneRepository.findAll();
    }

    public List<Zone> findByBergerie(Long bergerieId) {
        return zoneRepository.findByBergerieId(bergerieId);
    }

    public Zone findById(Long id) {
        return zoneRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Zone non trouvée"));
    }

    public Zone save(Zone zone) {
        return zoneRepository.save(zone);
    }

    public void delete(Long id) {
        zoneRepository.deleteById(id);
    }
}
