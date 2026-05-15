package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDetailResponseDTO {
    private UUID attemptId;
    private UUID quizId;
    private UUID hocVienId;
    private String tenHocVien;
    private String maHocVien;
    private Float diem;
    private Integer soCauDung;
    private Integer tongCauHoi;
    private Integer usedTime;
    private Integer remainingTime;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<QuizAttemptAnswerDetailDTO> answers;
}
