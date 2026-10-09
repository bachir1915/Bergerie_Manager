package com.bergerie.controller.api;

import com.bergerie.dto.EleveurAdminDto;
import com.bergerie.dto.InscriptionEleveurRequest;
import com.bergerie.dto.VeterinaireAdminDto;
import com.bergerie.entity.Eleveur;
import com.bergerie.entity.Veterinaire;
import com.bergerie.service.EleveurService;
import com.bergerie.service.VeterinaireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Inscriptions publiques.
 *  - Éleveur    : compte actif immédiatement.
 *  - Vétérinaire : compte créé au statut EN_ATTENTE, justificatif obligatoire, validé par l'Admin.
 *
 * Il n'existe volontairement aucune inscription publique pour le rôle ADMIN.
 */
@RestController
@RequestMapping("/api/auth/inscription")
@RequiredArgsConstructor
public class InscriptionController {

    private final VeterinaireService veterinaireService;
    private final EleveurService eleveurService;

    @PostMapping("/eleveur")
    public ResponseEntity<EleveurAdminDto> inscrireEleveur(@Valid @RequestBody InscriptionEleveurRequest request) {
        Eleveur e = eleveurService.inscrire(request.nom(), request.prenom(), request.email(),
                request.telephone());
        // La réponse contient le codeConnexion : c'est ce que l'éleveur devra saisir pour se connecter.
        return ResponseEntity.status(HttpStatus.CREATED).body(EleveurAdminDto.from(e));
    }

    @PostMapping(value = "/veterinaire", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VeterinaireAdminDto> inscrireVeterinaire(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String email,
            @RequestParam String telephone,
            @RequestParam(required = false) String specialite,
            @RequestParam("justificatif") MultipartFile justificatif) {

        Veterinaire v = veterinaireService.inscrire(nom, prenom, email, telephone, specialite, justificatif);

        return ResponseEntity.status(HttpStatus.CREATED).body(VeterinaireAdminDto.from(v));
    }
}
