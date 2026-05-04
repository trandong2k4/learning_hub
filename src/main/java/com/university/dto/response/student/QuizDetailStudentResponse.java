package com.university.dto.response.student;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class QuizDetailStudentResponse {

    private UUID id;
    private String tieuDe;
    private String moTa;

    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;

    private Integer thoiGianLam;
    private Integer remainingTime;

    private List<QuestionStudentResponse> questions;
}