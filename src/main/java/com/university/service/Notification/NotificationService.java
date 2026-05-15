package com.university.service.Notification;

import java.util.List;
import java.util.UUID;

import com.university.dto.request.Notification.NotificationRequest;
import com.university.dto.response.Notification.NotificationResponse;

public interface NotificationService {

    void sendNotification(NotificationRequest request, UUID senderId);

    List<NotificationResponse> getMyNotifications(UUID userId);

    List<NotificationResponse> getUnreadNotifications(UUID userId);

    void markAsRead(UUID thongBaoId, UUID userId);
}
