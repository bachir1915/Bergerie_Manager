package com.bergerie.service;

import com.bergerie.entity.*;
import com.bergerie.enums.*;
import com.bergerie.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlerteService {

    private final AlerteRepository alerteRepository;
    private final UtilisateurRepository utilisateurRepository;

    public List<Alerte> findAll() {
        return alerteRepository.findAll();
    }

    public List<Alerte> findRecentes() {
        return alerteRepository.findTop10ByOrderByDateHeureDesc();
    }

    public List<Alerte> findByUtilisateur(Long userId) {
        return alerteRepository.findByUtilisateurDestIdOrderByDateHeureDesc(userId);
    }

    public List<Alerte> findNonLues() {
        return alerteRepository.findByStatutAlerteOrderByDateHeureDesc(StatutAlerte.NON_LUE);
    }

    public long countNonLues() {
        return alerteRepository.countByStatutAlerte(StatutAlerte.NON_LUE);
    }

    public long countNonLuesPourUtilisateur(Long userId) {
        return alerteRepository.countByUtilisateurDestIdAndStatutAlerte(userId, StatutAlerte.NON_LUE);
    }

    @Transactional
    public void marquerLue(Long alerteId) {
        Alerte alerte = alerteRepository.findById(alerteId)
            .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
        alerte.setStatutAlerte(StatutAlerte.LUE);
        alerteRepository.save(alerte);
    }

    @Transactional
    public void marquerTraitee(Long alerteId) {
        Alerte alerte = alerteRepository.findById(alerteId)
            .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
        alerte.setStatutAlerte(StatutAlerte.TRAITEE);
        alerteRepository.save(alerte);
    }

    @Transactional
    public Alerte creerAlerte(Animal animal, Capteur capteur, TypeAlerte type,
                               String message, NiveauAlerte niveau) {
        Alerte alerte = new Alerte();
        alerte.setAnimal(animal);
        alerte.setCapteur(capteur);
        alerte.setType(type);
        alerte.setMessage(message);
        alerte.setNiveau(niveau);
        alerte.setStatutAlerte(StatutAlerte.NON_LUE);
        alerte.setDateHeure(LocalDateTime.now());

        // Notifier tous les éleveurs et vétérinaires
        List<Utilisateur> tous = utilisateurRepository.findAll();
        for (Utilisateur u : tous) {
            Alerte alerteUser = new Alerte();
            alerteUser.setAnimal(animal);
            alerteUser.setCapteur(capteur);
            alerteUser.setUtilisateurDest(u);
            alerteUser.setType(type);
            alerteUser.setMessage(message);
            alerteUser.setNiveau(niveau);
            alerteUser.setStatutAlerte(StatutAlerte.NON_LUE);
            alerteUser.setDateHeure(LocalDateTime.now());
            alerteRepository.save(alerteUser);
            log.warn("[ALERTE] {} - {} → {}", type, message, u.getEmail());
        }
        return alerte;
    }
}
