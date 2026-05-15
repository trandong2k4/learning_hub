package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponseDTO {
    private UUID submissionId;
    private UUID assignmentId;
    private String assignmentTitle;
    private UUID studentId;
    private String studentName;
    private String studentCode;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private Float grade; // điểm số nếu đã chấm
    private String feedback; // nhận xét nếu có
}
