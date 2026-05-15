package com.university.controller.Notification;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.university.dto.request.Notification.NotificationRequest;
import com.university.exception.students.ResourceNotFoundException;
import com.university.security.CustomUserDetails;
import com.university.service.Notification.NotificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("User chưa đăng nhập");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails user)) {
            throw new RuntimeException("Không lấy được userId từ SecurityContext");
        }

        return user.getUserId();
    }

    @GetMapping("/read")
    public ResponseEntity<?> getMyNotifications() {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(getCurrentUserId()));
    }

    @PostMapping("/admin")
    public ResponseEntity<?> send(@RequestBody @Valid NotificationRequest request) {

        notificationService.sendNotification(request, getCurrentUserId());

        return ResponseEntity.ok(Map.of(
                "message", "Gửi thông báo thành công"));
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUnread() {

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(getCurrentUserId()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable UUID id) {

        notificationService.markAsRead(id, getCurrentUserId());
        return ResponseEntity.ok(Map.of(
                "message", "Đã đánh dấu là đã đọc"));
    }
}
