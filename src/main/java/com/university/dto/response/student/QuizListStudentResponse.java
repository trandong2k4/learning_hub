package com.university.dto.response.student;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import com.university.enums.QuizStatusEnum;
@Data
public class QuizListStudentResponse {

    private UUID id;
    private String tieuDe;

    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;

    private Integer thoiGianLam;

    private QuizStatusEnum status; // UPCOMING | DOING | DONE | EXPIRED
}
