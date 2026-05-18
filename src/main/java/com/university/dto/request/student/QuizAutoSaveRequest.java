package com.university.dto.request.student;

import java.util.List;

import lombok.Data;

@Data
public class QuizAutoSaveRequest {
    private List<QuizAnswerSaveItemRequest> answers;
    private Integer usedTime;
    private String eventType;
    private String eventData;
}
