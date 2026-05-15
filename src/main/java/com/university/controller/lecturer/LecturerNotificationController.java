package com.university.controller.lecturer;

import com.university.annotation.RequirePermission;
import com.university.dto.request.lecturer.NotificationRequestDTO;
import com.university.dto.response.lecturer.NotificationDetailResponseDTO;
import com.university.dto.response.lecturer.NotificationResponseDTO;
import com.university.service.lecturer.LecturerNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lecturer")
@CrossOrigin
@RequiredArgsConstructor
@RequirePermission("LECTURER_NOTIFICATION")
public class LecturerNotificationController {

    private final LecturerNotificationService notificationService;

    @PostMapping("/notifications")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_NOTIFICATION')")
    public ResponseEntity<NotificationResponseDTO> sendNotification(
            @Valid @RequestBody NotificationRequestDTO request,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(notificationService.sendNotification(userId, request));
    }

    @GetMapping("/notifications")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_NOTIFICATION')")
    public ResponseEntity<List<NotificationDetailResponseDTO>> getMyNotifications(@RequestParam UUID userId) {
        return ResponseEntity.ok(notificationService.getMyNotifications(userId));
    }

    @DeleteMapping("/notifications/{notificationId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_NOTIFICATION')")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable UUID notificationId,
            @RequestParam UUID userId) {
        notificationService.deleteNotification(userId, notificationId);
        return ResponseEntity.noContent().build();
    }
}
