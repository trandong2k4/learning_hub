package com.university.dto.request.student;

import lombok.Data;
import java.util.UUID;

import com.university.enums.AttemptActionEnum;

@Data
public class QuizLogStudentRequest {

    private UUID quizAttemptId;
    private UUID questionId;

    private AttemptActionEnum action;

    private String value;
}