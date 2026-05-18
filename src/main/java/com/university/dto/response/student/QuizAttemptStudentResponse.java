package com.university.dto.response.student;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Data;

@Data
public class QuizAttemptStudentResponse {
    private UUID attemptId;
    private UUID quizId;
    private String tieuDe;
    private Integer remainingTime;
    private Integer usedTime;
    private Boolean submitted;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<QuestionStudentResponse> questions;
    private Map<UUID, UUID> selectedAnswers;
    private Map<UUID, String> textAnswers;
}
