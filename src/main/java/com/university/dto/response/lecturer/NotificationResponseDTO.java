package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private UUID id;
    private String tieuDe;
    private String noiDung;
    private String fileThongBao;
    private LocalDateTime createdAt;
}
