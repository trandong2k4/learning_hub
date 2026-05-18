package com.university.dto.request.student;

import java.util.UUID;

import lombok.Data;

@Data
public class QuizAnswerSaveItemRequest {
    private UUID questionId;
    private UUID answerId;
    private String textAnswer;
    private Integer timeOnQuestion;
}
