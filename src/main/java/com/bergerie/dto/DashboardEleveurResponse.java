package com.bergerie.dto;

import java.util.List;

public record DashboardEleveurResponse(
    long totalAnimaux,
    long animauxSains,
    long animauxMalades,
    long animauxCritiques,
    long alertesNonLues,
    List<AlerteDto> dernieresAlertes,
    List<DonneeEnvDto> donneesMesures,
    List<StatEntry> statsSante,
    List<StatEntry> statsRace
) {
}
