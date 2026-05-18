package com.university.dto.response.lecturer;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptAnswerDetailDTO {
    private UUID questionId;
    private String noiDung;
    private Float diem;
    private UUID selectedAnswerId;
    private String selectedKeyAnswers;
    private String selectedAnswerText;
    private String textAnswer;
    private Boolean selectedCorrect;
    private java.math.BigDecimal scoreReceived;
    private List<QuizAttemptAnswerOptionDTO> answers;
    private List<QuizAttemptAnswerLogDTO> answerLogs;
}
