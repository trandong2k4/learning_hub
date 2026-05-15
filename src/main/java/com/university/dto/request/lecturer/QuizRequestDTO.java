package com.university.dto.request.lecturer;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequestDTO {
    @NotNull(message = "Lớp học phần không được để trống")
    private java.util.UUID lopHocPhanId;

    @NotEmpty(message = "Tiêu đề không được để trống")
    private String tieuDe;

    private String moTa;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime thoiGianBatDau;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime thoiGianKetThuc;

    @NotNull(message = "Thời gian làm không được để trống")
    private Integer thoiGianLam;

    private Integer soLanLam;

    private Boolean trinhTrang;

    @NotEmpty(message = "Phải có ít nhất 1 câu hỏi")
    @Valid
    private List<QuestionRequestDTO> questions;
}
