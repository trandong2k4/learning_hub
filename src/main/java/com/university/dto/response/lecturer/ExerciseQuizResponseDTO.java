package com.university.dto.response.lecturer;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseQuizResponseDTO {
    private UUID exerciseId;
    private String tenExercise;
    private Integer questionCount;
}