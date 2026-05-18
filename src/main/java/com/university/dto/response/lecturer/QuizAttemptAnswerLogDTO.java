package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptAnswerLogDTO {
    private UUID questionId;
    private UUID oldAnswerId;
    private UUID newAnswerId;
    private String oldTextAnswer;
    private String newTextAnswer;
    private Integer timeOnQuestion;
    private LocalDateTime changedAt;
}
