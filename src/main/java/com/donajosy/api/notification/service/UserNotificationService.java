package com.donajosy.api.notification.service;

import com.donajosy.api.auth.entity.User;
import com.donajosy.api.auth.repository.UserRepository;
import com.donajosy.api.notification.dto.UserNotificationResponse;
import com.donajosy.api.notification.entity.UserNotification;
import com.donajosy.api.notification.repository.UserNotificationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserNotificationService {
    private final UserNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public UserNotificationService(UserNotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<UserNotificationResponse> unread(Authentication authentication) {
        User user = currentUser(authentication);
        return notificationRepository.findByUser_IdAndReadFalseOrderByCreatedAtDesc(user.getId())
                .stream().map(UserNotificationResponse::from).toList();
    }

    public void markRead(UUID notificationId, Authentication authentication) {
        User user = currentUser(authentication);
        UserNotification notification = notificationRepository.findByIdAndUser_Id(notificationId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private User currentUser(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getRole() == null || !user.getRole().name().equals("USER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only users can access notifications");
        }
        return user;
    }
}
