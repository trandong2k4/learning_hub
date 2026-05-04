package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DashboardStudentNotificationItemResponse {

    private UUID id;
    private String tieuDe;
    private LocalDateTime createdAt;
    private Boolean daNhan;
}
