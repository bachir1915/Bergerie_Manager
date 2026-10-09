package com.bergerie.dto;

import java.util.List;

public record DashboardVeterinaireResponse(
    long totalAnimaux,
    long animauxMalades,
    long animauxCritiques,
    long totalVaccinations,
    long totalTraitements,
    long totalAnalyses,
    long totalConsultations,
    List<AlerteDto> dernieresAlertes,
    List<StatEntry> statsSante
) {
}
