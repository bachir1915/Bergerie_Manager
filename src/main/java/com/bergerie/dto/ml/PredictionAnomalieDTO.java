package com.bergerie.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Réponse retournée par le microservice Python FastAPI suite à l'analyse
 * par le modèle IsolationForest.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionAnomalieDTO {

    @JsonProperty("animal_id")
    private Long animalId;

    @JsonProperty("score_anomalie")
    private Double scoreAnomalie;

    @JsonProperty("est_anomalie")
    private Boolean estAnomalie;

    @JsonProperty("niveau_risque")
    private String niveauRisque;

    @JsonProperty("details")
    private String details;
}
