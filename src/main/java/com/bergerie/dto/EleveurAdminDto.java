package com.bergerie.dto;

import com.bergerie.entity.Eleveur;

public record EleveurAdminDto(Long id, String nom, String prenom, String email,
                               String telephone, String numEleveur, String codeConnexion) {

    public static EleveurAdminDto from(Eleveur e) {
        return new EleveurAdminDto(e.getId(), e.getNom(), e.getPrenom(), e.getEmail(),
                e.getTelephone(), e.getNumEleveur(), e.getCodeConnexion());
    }
}
