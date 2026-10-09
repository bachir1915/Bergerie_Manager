package com.bergerie.service;

import com.bergerie.entity.Alerte;
import com.bergerie.entity.Notification;
import com.bergerie.entity.Utilisateur;
import com.bergerie.enums.CanalNotif;
import com.bergerie.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> findByUtilisateur(Long userId) {
        return notificationRepository.findByUtilisateurDestIdOrderByDateEnvoiDesc(userId);
    }

    public List<Notification> findNonLuesPourUtilisateur(Long userId) {
        return notificationRepository.findByUtilisateurDestIdAndDateLectureIsNullOrderByDateEnvoiDesc(userId);
    }

    public long countNonLuesPourUtilisateur(Long userId) {
        return notificationRepository.countByUtilisateurDestIdAndDateLectureIsNull(userId);
    }

    @Transactional
    public Notification creerNotification(Alerte alerte, Utilisateur destinataire, CanalNotif canal) {
        Notification notification = new Notification(alerte, destinataire, canal != null ? canal : CanalNotif.PUSH);
        Notification saved = notificationRepository.save(notification);
        log.info("[NOTIFICATION] Créée pour {} (canal: {}, alerte ID: {})",
                destinataire.getEmail(), notification.getCanal(), alerte.getId());
        return saved;
    }

    @Transactional
    public void marquerLue(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée: " + notificationId));
        notification.setDateLecture(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Transactional
    public void marquerToutesLuesPourUtilisateur(Long userId) {
        List<Notification> nonLues = notificationRepository.findByUtilisateurDestIdAndDateLectureIsNullOrderByDateEnvoiDesc(userId);
        LocalDateTime maintenant = LocalDateTime.now();
        for (Notification n : nonLues) {
            n.setDateLecture(maintenant);
            notificationRepository.save(n);
        }
    }
}
