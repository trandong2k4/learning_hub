package com.university.dto.response.student;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
public class QuizStartStudentResponse {

    private UUID attemptId;
    private Integer remainingTime;
    private LocalDateTime startTime;
}