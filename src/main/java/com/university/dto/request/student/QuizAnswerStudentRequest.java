package com.university.dto.request.student;

import lombok.Data;
import java.util.UUID;

@Data
public class QuizAnswerStudentRequest {

    private UUID quizAttemptId;
    private UUID questionId;
    private UUID answerId;
}