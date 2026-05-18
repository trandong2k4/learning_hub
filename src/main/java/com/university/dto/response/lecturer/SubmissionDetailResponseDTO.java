package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDetailResponseDTO {
    private UUID submissionId;
    private UUID assignmentId;
    private String assignmentTitle;
    private UUID studentId;
    private String studentName;
    private String studentCode;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private Float grade;
    private String feedback;
    private List<QuestionAnswerDTO> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionAnswerDTO {
        private UUID questionId;
        private String questionContent;
        private Boolean isMultipleChoice;
        private Boolean isMultipleAnswer;
        private Float maxScore;
        private Float earnedScore;
        private String submittedAnswer; // for essay: noiDungTuLuan
        private List<AnswerOptionDTO> options; // for multiple choice: list of options
        private UUID selectedAnswerId; // for multiple choice: which answer student selected
        private Boolean isCorrect;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerOptionDTO {
        private UUID answerId;
        private String content;
        private Boolean isCorrect;
        private Boolean isSelected; // was this selected by student
    }
}
