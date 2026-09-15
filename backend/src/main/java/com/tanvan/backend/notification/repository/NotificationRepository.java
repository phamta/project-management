// notification/repository/NotificationRepository.java
package com.tanvan.backend.notification.repository;

import com.tanvan.backend.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    Page<Notification> findByRecipient_IdOrderByCreatedAtDesc(String recipientId, Pageable pageable);

    Page<Notification> findByRecipient_IdAndIsReadFalseOrderByCreatedAtDesc(String recipientId, Pageable pageable);

    long countByRecipient_IdAndIsReadFalse(String recipientId);

    Optional<Notification> findByIdAndRecipient_Id(String id, String recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.recipient.id = :recipientId")
    void markAsRead(@Param("id") String id, @Param("recipientId") String recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient.id = :recipientId AND n.isRead = false")
    void markAllAsRead(@Param("recipientId") String recipientId);
}
