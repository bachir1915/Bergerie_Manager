package com.bergerie.service;

import com.bergerie.dto.BergerieAdminDto;
import com.bergerie.repository.BergerieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Consultation des bergeries pour l'Admin.
 * La transaction reste ouverte le temps de parcourir les collections lazy (zones, animaux).
 */
@Service
@RequiredArgsConstructor
public class BergerieAdminService {

    private final BergerieRepository bergerieRepository;

    @Transactional(readOnly = true)
    public List<BergerieAdminDto> listerToutesDetaillees() {
        return bergerieRepository.findAllAvecEleveur().stream()
                .map(BergerieAdminDto::from)
                .toList();
    }
}
