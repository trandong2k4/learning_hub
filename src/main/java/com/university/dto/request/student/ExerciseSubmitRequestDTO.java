package com.university.dto.request.student;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExerciseSubmitRequestDTO {
    @NotNull(message = "Mã bài tập không được để trống")
    private UUID exerciseId;

    private String fileExerciseUrl;

    @Valid
    private List<ExerciseQuestionAnswerDTO> answers;

    @Data
    public static class ExerciseQuestionAnswerDTO {
        @NotNull(message = "Mã câu hỏi không được để trống")
        private UUID questionId;

        private UUID answerId;

        private String noiDungTuLuan;
    }
}
