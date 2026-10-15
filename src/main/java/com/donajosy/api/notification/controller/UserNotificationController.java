package com.donajosy.api.notification.controller;

import com.donajosy.api.notification.dto.UserNotificationResponse;
import com.donajosy.api.notification.service.UserNotificationService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class UserNotificationController {
    private final UserNotificationService notificationService;

    public UserNotificationController(UserNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/unread")
    public List<UserNotificationResponse> unread(Authentication authentication) {
        return notificationService.unread(authentication);
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable UUID id, Authentication authentication) {
        notificationService.markRead(id, authentication);
    }
}
