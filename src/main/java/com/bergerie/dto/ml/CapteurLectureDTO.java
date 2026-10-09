package com.bergerie.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Données agrégées horaires issues des capteurs ESP32 d'un animal
 * transmises au microservice Python FastAPI pour détection d'anomalies.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapteurLectureDTO {

    @JsonProperty("animal_id")
    private Long animalId;

    @JsonProperty("heure_jour")
    private Integer heureJour;

    @JsonProperty("temperature_ambiante")
    private Double temperatureAmbiante;

    @JsonProperty("humidite")
    private Double humidite;

    @JsonProperty("qualite_air")
    private Double qualiteAir;

    @JsonProperty("distance_parcourue_km")
    private Double distanceParcourueKm;

    @JsonProperty("vitesse_moyenne_kmh")
    private Double vitesseMoyenneKmh;
}
