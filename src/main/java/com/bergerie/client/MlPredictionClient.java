package com.bergerie.client;

import com.bergerie.dto.ml.CapteurLectureDTO;
import com.bergerie.dto.ml.PredictionAnomalieDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

/**
 * Client HTTP dédié aux échanges avec le microservice Python FastAPI de détection d'anomalies.
 * Utilise WebClient (non bloquant avec fallback robuste en cas d'indisponibilité).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MlPredictionClient {

    private final WebClient mlWebClient;

    /**
     * Envoie les données horaires agrégées d'un animal pour obtenir la prédiction d'anomalie.
     * En cas d'erreur réseau, de timeout ou d'erreur 503, la méthode logge l'erreur et
     * renvoie Optional.empty() sans lever d'exception bloquante.
     *
     * @param lecture Données horaires des capteurs (ESP32)
     * @return Optional contenant le DTO de prédiction si succès, sinon Optional.empty()
     */
    public Optional<PredictionAnomalieDTO> predireAnomalie(CapteurLectureDTO lecture) {
        try {
            PredictionAnomalieDTO resultat = mlWebClient.post()
                    .uri("/predict")
                    .bodyValue(lecture)
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError, response -> {
                        log.warn("[ML CLIENT] Le microservice Python a renvoyé une erreur serveur HTTP {} (modèle potentiellement non chargé)",
                                response.statusCode());
                        return response.createException();
                    })
                    .onStatus(HttpStatusCode::is4xxClientError, response -> {
                        log.error("[ML CLIENT] Erreur client HTTP {} envoyée au microservice Python", response.statusCode());
                        return response.createException();
                    })
                    .bodyToMono(PredictionAnomalieDTO.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            return Optional.ofNullable(resultat);

        } catch (WebClientResponseException e) {
            log.warn("[ML CLIENT] Réponse HTTP {} reçue du microservice pour l'animal ID {} : {}",
                    e.getStatusCode(), lecture.getAnimalId(), e.getResponseBodyAsString());
            return Optional.empty();
        } catch (Exception e) {
            log.warn("[ML CLIENT INDISPONIBLE] Impossible de contacter le microservice Python (URL: /predict) pour l'animal ID {} : {}",
                    lecture.getAnimalId(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Vérifie la disponibilité et la santé du microservice Python.
     *
     * @return true si le service répond et que le modèle est chargé, false sinon.
     */
    public boolean verifierSante() {
        try {
            String reponse = mlWebClient.get()
                    .uri("/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();
            return reponse != null && reponse.contains("\"model_loaded\":true");
        } catch (Exception e) {
            log.debug("[ML CLIENT] Le microservice Python n'est pas joignable : {}", e.getMessage());
            return false;
        }
    }
}
