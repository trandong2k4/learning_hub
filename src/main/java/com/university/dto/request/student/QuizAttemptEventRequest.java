package com.university.dto.request.student;

import java.util.UUID;

import com.university.enums.AttemptActionEnum;

import lombok.Data;

@Data
public class QuizAttemptEventRequest {
    private AttemptActionEnum action;
    private UUID questionId;
    private String value;
    private String eventData;
}
