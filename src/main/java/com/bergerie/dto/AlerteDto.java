package com.bergerie.dto;

import java.time.LocalDateTime;

public record AlerteDto(Long id, String type, String message, String niveau,
                         String statutAlerte, LocalDateTime dateHeure, String animalNom) {
}
