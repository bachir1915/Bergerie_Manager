package com.bergerie.repository;

import com.bergerie.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUtilisateurDestIdOrderByDateEnvoiDesc(Long userId);

    List<Notification> findByUtilisateurDestIdAndDateLectureIsNullOrderByDateEnvoiDesc(Long userId);

    long countByUtilisateurDestIdAndDateLectureIsNull(Long userId);

    List<Notification> findByAlerteId(Long alerteId);
}
