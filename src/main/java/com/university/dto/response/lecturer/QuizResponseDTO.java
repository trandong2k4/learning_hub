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
public class QuizResponseDTO {
    private UUID quizId;
    private UUID lopHocPhanId;
    private String tenLopHocPhan;
    private String tieuDe;
    private String moTa;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private Integer thoiGianLam;
    private Integer soLanLam;
    private Boolean trinhTrang;
    private LocalDateTime createdAt;
    private Integer questionCount;
    private List<QuestionResponseDTO> questions;
}
