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
public class AssignmentResponseDTO {
    private UUID id;
    private String tieuDe;
    private String moTa;
    private LocalDateTime createdAt;
    private UUID lopHocPhanId;
    private int submissionCount;
    private String fileExerciseUrl;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private int questionCount;
    private List<QuestionResponseDTO> questions;
    private Integer gioiHanLanLam;

    public AssignmentResponseDTO(UUID id, String tieuDe, String moTa, LocalDateTime createdAt,
                                 UUID lopHocPhanId, int submissionCount, String fileExerciseUrl,
                                 LocalDateTime thoiGianBatDau, LocalDateTime thoiGianKetThuc) {
        this(id, tieuDe, moTa, createdAt, lopHocPhanId, submissionCount, fileExerciseUrl,
                thoiGianBatDau, thoiGianKetThuc, 0, null, 1);
    }
}
