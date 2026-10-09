package com.bergerie.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Renvoie des erreurs JSON propres pour toutes les routes /api/** au lieu d'une redirection
 * vers la page de login (comportement par défaut du forward vers /error).
 */
@RestControllerAdvice(basePackages = "com.bergerie.controller.api")
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(ResponseStatusException ex) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> champs = new java.util.HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(e -> champs.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of(
            "status", 400,
            "message", "Requête invalide.",
            "champs", champs));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }
}
