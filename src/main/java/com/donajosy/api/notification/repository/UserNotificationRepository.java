package com.donajosy.api.notification.repository;

import com.donajosy.api.notification.entity.UserNotification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationRepository extends JpaRepository<UserNotification, UUID> {
    List<UserNotification> findByUser_IdAndReadFalseOrderByCreatedAtDesc(UUID userId);
    Optional<UserNotification> findByIdAndUser_Id(UUID id, UUID userId);
}
