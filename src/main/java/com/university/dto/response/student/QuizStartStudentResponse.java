package com.university.dto.response.student;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Data
public class QuizStartStudentResponse {

    private UUID attemptId;
    private UUID quizId;
    private Integer remainingTime;
    private Integer usedTime;
    private LocalDateTime startTime;
    private List<QuestionStudentResponse> questions;
    private Map<UUID, UUID> selectedAnswers;
    private Map<UUID, String> textAnswers;
}
