package com.bergerie.service;

import com.bergerie.dto.ConnexionDto;
import com.bergerie.entity.Bergerie;
import com.bergerie.entity.ConnexionVeterinaire;
import com.bergerie.entity.Veterinaire;
import com.bergerie.enums.StatutConnexion;
import com.bergerie.repository.ConnexionVeterinaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cycle de vie de la connexion vétérinaire - bergerie :
 * demande (vétérinaire) -> acceptation, refus ou annulation (éleveur).
 */
@Service
@RequiredArgsConstructor
public class ConnexionService {

    private final ConnexionVeterinaireRepository connexionRepository;

    /**
     * Appelée quand un vétérinaire présente le code d'une bergerie.
     * Ne retourne que si la connexion est acceptée ; sinon enregistre la demande et refuse l'accès (403).
     */
    // noRollbackFor : la demande enregistrée doit survivre à la réponse 403.
    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void exigerAcceptee(Veterinaire vet, Bergerie bergerie) {
        var existante = connexionRepository.findByVeterinaireIdAndBergerieId(vet.getId(), bergerie.getId());
        if (existante.isPresent() && existante.get().getStatut() == StatutConnexion.ACCEPTEE) {
            return;
        }
        String message;
        if (existante.isPresent() && existante.get().getStatut() == StatutConnexion.EN_ATTENTE) {
            message = "Votre demande est en attente de validation par l'éleveur de la bergerie « "
                    + bergerie.getNom() + " ».";
        } else {
            ConnexionVeterinaire c = existante.orElseGet(ConnexionVeterinaire::new);
            c.setVeterinaire(vet);
            c.setBergerie(bergerie);
            c.setStatut(StatutConnexion.EN_ATTENTE);
            c.setDateDemande(LocalDateTime.now());
            c.setDateReponse(null);
            connexionRepository.save(c);
            message = "Demande de connexion envoyée à l'éleveur de la bergerie « " + bergerie.getNom()
                    + " ». Vous pourrez vous connecter dès qu'il l'aura acceptée.";
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    /** Vérifie, à chaque requête d'un vétérinaire, que sa connexion à la bergerie est toujours acceptée. */
    @Transactional(readOnly = true)
    public boolean estAcceptee(String emailVeterinaire, Long bergerieId) {
        return connexionRepository.existsByVeterinaireEmailAndBergerieIdAndStatut(
                emailVeterinaire, bergerieId, StatutConnexion.ACCEPTEE);
    }

    @Transactional(readOnly = true)
    public List<ConnexionDto> lister(String emailEleveur) {
        return connexionRepository.findByEleveurEmail(emailEleveur).stream().map(ConnexionDto::from).toList();
    }

    @Transactional
    public ConnexionDto accepter(Long id, String emailEleveur) {
        return changer(id, emailEleveur, StatutConnexion.EN_ATTENTE, StatutConnexion.ACCEPTEE,
                "Seule une demande en attente peut être acceptée.");
    }

    @Transactional
    public ConnexionDto refuser(Long id, String emailEleveur) {
        return changer(id, emailEleveur, StatutConnexion.EN_ATTENTE, StatutConnexion.REFUSEE,
                "Seule une demande en attente peut être refusée.");
    }

    @Transactional
    public ConnexionDto annuler(Long id, String emailEleveur) {
        return changer(id, emailEleveur, StatutConnexion.ACCEPTEE, StatutConnexion.ANNULEE,
                "Seule une connexion acceptée peut être annulée.");
    }

    private ConnexionDto changer(Long id, String emailEleveur, StatutConnexion attendu,
                                 StatutConnexion nouveau, String erreur) {
        ConnexionVeterinaire c = connexionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable."));
        Bergerie b = c.getBergerie();
        if (b.getEleveur() == null || !emailEleveur.equals(b.getEleveur().getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette bergerie ne vous appartient pas.");
        }
        if (c.getStatut() != attendu) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, erreur);
        }
        c.setStatut(nouveau);
        c.setDateReponse(LocalDateTime.now());
        return ConnexionDto.from(connexionRepository.save(c));
    }
}
