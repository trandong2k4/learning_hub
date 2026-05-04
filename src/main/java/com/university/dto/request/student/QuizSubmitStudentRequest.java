package com.university.dto.request.student;

import java.util.UUID;
import lombok.Data;
@Data
public class QuizSubmitStudentRequest {
    private UUID quizAttemptId;
}
