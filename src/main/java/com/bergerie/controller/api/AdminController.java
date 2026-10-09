package com.bergerie.controller.api;

import com.bergerie.dto.BergerieAdminDto;
import com.bergerie.dto.EleveurAdminDto;
import com.bergerie.dto.MotifRequest;
import com.bergerie.dto.VeterinaireAdminDto;
import com.bergerie.enums.StatutValidation;
import com.bergerie.repository.EleveurRepository;
import com.bergerie.service.BergerieAdminService;
import com.bergerie.service.VeterinaireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Espace d'administration. Toutes les routes sont réservées au rôle ADMIN
 * (compte créé manuellement en base, aucune inscription publique).
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final VeterinaireService veterinaireService;
    private final BergerieAdminService bergerieAdminService;
    private final EleveurRepository eleveurRepository;

    // ---- Vétérinaires : consultation ----

    /**
     * Liste de tous les vétérinaires avec leurs informations complètes et leur justificatif.
     * Filtrable par statut : {@code /api/admin/veterinaires?statut=EN_ATTENTE}.
     */
    @GetMapping("/veterinaires")
    public List<VeterinaireAdminDto> listerVeterinaires(
            @RequestParam(required = false) StatutValidation statut) {
        return veterinaireService.lister(statut).stream()
                .map(VeterinaireAdminDto::from)
                .toList();
    }

    @GetMapping("/veterinaires/{id}")
    public VeterinaireAdminDto detailVeterinaire(@PathVariable Long id) {
        return VeterinaireAdminDto.from(veterinaireService.getOuErreur(id));
    }

    /** Téléchargement du justificatif fourni par le vétérinaire à son inscription. */
    @GetMapping("/veterinaires/{id}/justificatif")
    public ResponseEntity<Resource> telechargerJustificatif(@PathVariable Long id) {
        VeterinaireService.Justificatif j = veterinaireService.chargerJustificatif(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(j.typeContenu()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + j.nomFichier() + "\"")
                .body(j.resource());
    }

    // ---- Vétérinaires : transitions de statut ----

    @PostMapping("/veterinaires/{id}/valider")
    public VeterinaireAdminDto valider(@PathVariable Long id) {
        return VeterinaireAdminDto.from(veterinaireService.valider(id));
    }

    @PostMapping("/veterinaires/{id}/rejeter")
    public VeterinaireAdminDto rejeter(@PathVariable Long id, @Valid @RequestBody MotifRequest body) {
        return VeterinaireAdminDto.from(veterinaireService.rejeter(id, body.motif()));
    }

    @PostMapping("/veterinaires/{id}/suspendre")
    public VeterinaireAdminDto suspendre(@PathVariable Long id, @Valid @RequestBody MotifRequest body) {
        return VeterinaireAdminDto.from(veterinaireService.suspendre(id, body.motif()));
    }

    @PostMapping("/veterinaires/{id}/reactiver")
    public VeterinaireAdminDto reactiver(@PathVariable Long id) {
        return VeterinaireAdminDto.from(veterinaireService.reactiver(id));
    }

    // ---- Bergeries ----

    /** Liste complète des bergeries avec leurs informations détaillées (éleveur, téléphone, email...). */
    @GetMapping("/bergeries")
    public List<BergerieAdminDto> listerBergeries() {
        return bergerieAdminService.listerToutesDetaillees();
    }

    // ---- Éleveurs (lecture seule : l'Admin ne crée pas de comptes) ----

    @GetMapping("/eleveurs")
    public List<EleveurAdminDto> listerEleveurs() {
        return eleveurRepository.findAll().stream().map(EleveurAdminDto::from).toList();
    }
}
