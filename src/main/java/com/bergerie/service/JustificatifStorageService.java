package com.bergerie.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Stockage sur disque des justificatifs des vétérinaires.
 * Le répertoire est configurable via la propriété {@code app.justificatifs.dir}
 * (par défaut : {@code ./uploads/justificatifs}).
 */
@Service
public class JustificatifStorageService {

    private static final long TAILLE_MAX = 5 * 1024 * 1024; // 5 Mo
    private static final Set<String> TYPES_AUTORISES = Set.of(
            "application/pdf", "image/png", "image/jpeg", "image/jpg", "image/webp");

    private final Path racine;

    public JustificatifStorageService(
            @Value("${app.justificatifs.dir:uploads/justificatifs}") String dossier) {
        this.racine = Paths.get(dossier).toAbsolutePath().normalize();
    }

    @PostConstruct
    void init() {
        try {
            Files.createDirectories(racine);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de créer le répertoire de stockage des justificatifs", e);
        }
    }

    /**
     * Enregistre le fichier et retourne le chemin relatif (à stocker en base).
     */
    public String stocker(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le justificatif est obligatoire.");
        }
        if (fichier.getSize() > TAILLE_MAX) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Le justificatif dépasse 5 Mo.");
        }
        String type = fichier.getContentType();
        if (type == null || !TYPES_AUTORISES.contains(type.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Format non accepté : PDF, PNG, JPEG ou WEBP uniquement.");
        }

        String extension = extension(fichier.getOriginalFilename());
        String nom = UUID.randomUUID() + extension;
        Path cible = racine.resolve(nom).normalize();
        if (!cible.startsWith(racine)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nom de fichier invalide.");
        }
        try {
            Files.copy(fichier.getInputStream(), cible, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Échec de l'enregistrement du justificatif", e);
        }
        return nom;
    }

    /**
     * Charge le fichier stocké sous forme de {@link Resource} téléchargeable.
     */
    public Resource charger(String cheminRelatif) {
        try {
            Path fichier = racine.resolve(cheminRelatif).normalize();
            if (!fichier.startsWith(racine)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chemin invalide.");
            }
            Resource resource = new UrlResource(fichier.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Justificatif introuvable sur le disque.");
            }
            return resource;
        } catch (java.net.MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Chemin de justificatif invalide.");
        }
    }

    public void supprimer(String cheminRelatif) {
        if (cheminRelatif == null || cheminRelatif.isBlank()) {
            return;
        }
        try {
            Path fichier = racine.resolve(cheminRelatif).normalize();
            if (fichier.startsWith(racine)) {
                Files.deleteIfExists(fichier);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Échec de la suppression du justificatif", e);
        }
    }

    private String extension(String nomOriginal) {
        if (nomOriginal == null) {
            return "";
        }
        int point = nomOriginal.lastIndexOf('.');
        return point >= 0 ? nomOriginal.substring(point).toLowerCase() : "";
    }
}
