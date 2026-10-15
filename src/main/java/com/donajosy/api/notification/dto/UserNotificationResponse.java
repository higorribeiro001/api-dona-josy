package com.donajosy.api.notification.dto;

import com.donajosy.api.notification.entity.UserNotification;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserNotificationResponse(UUID id, String message, LocalDateTime createdAt, boolean read) {
    public static UserNotificationResponse from(UserNotification notification) {
        return new UserNotificationResponse(notification.getId(), notification.getMessage(),
                notification.getCreatedAt(), notification.isRead());
    }
}
