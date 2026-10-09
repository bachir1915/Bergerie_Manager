package com.bergerie.service;

import com.bergerie.entity.Alerte;
import com.bergerie.entity.Animal;
import com.bergerie.entity.Utilisateur;
import com.bergerie.enums.CanalNotif;
import com.bergerie.enums.NiveauAlerte;
import com.bergerie.enums.StatutAlerte;
import com.bergerie.enums.TypeAlerte;
import com.bergerie.repository.AlerteRepository;
import com.bergerie.repository.UtilisateurRepository;
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
    private final NotificationService notificationService;

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
        return alerteRepository.findByStatutAlerteOrderByDateHeureDesc(StatutAlerte.NOUVELLE);
    }

    public long countNonLues() {
        return alerteRepository.countByStatutAlerte(StatutAlerte.NOUVELLE);
    }

    public long countNonLuesPourUtilisateur(Long userId) {
        return notificationService.countNonLuesPourUtilisateur(userId);
    }

    @Transactional
    public void marquerLue(Long alerteId) {
        Alerte alerte = alerteRepository.findById(alerteId)
            .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
        alerte.setStatutAlerte(StatutAlerte.EN_COURS);
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
    public Alerte creerAlerte(Animal animal, TypeAlerte type,
                               String message, NiveauAlerte niveau) {
        Alerte alerte = new Alerte();
        alerte.setAnimal(animal);
        alerte.setType(type);
        alerte.setMessage(message);
        alerte.setNiveau(niveau != null ? niveau : NiveauAlerte.NORMAL);
        alerte.setStatutAlerte(StatutAlerte.NOUVELLE);
        alerte.setDateHeure(LocalDateTime.now());
        Alerte saved = alerteRepository.save(alerte);

        // Notifier tous les éleveurs et vétérinaires via Notification
        List<Utilisateur> tous = utilisateurRepository.findAll();
        for (Utilisateur u : tous) {
            notificationService.creerNotification(saved, u, CanalNotif.PUSH);
            log.warn("[ALERTE] {} - {} → notification pour {}", type, message, u.getEmail());
        }
        return saved;
    }
}
