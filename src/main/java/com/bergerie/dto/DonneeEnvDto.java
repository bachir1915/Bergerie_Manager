package com.bergerie.dto;

import java.time.LocalDateTime;

public record DonneeEnvDto(Long id, String zoneNom, Double temperature, Double humidite,
                            Double qualiteAir, LocalDateTime dateHeure) {
}
