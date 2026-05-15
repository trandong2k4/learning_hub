package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DashboardStudentNotificationItemResponse {

    private UUID id;
    private String tieuDe;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Boolean daNhan;
}
