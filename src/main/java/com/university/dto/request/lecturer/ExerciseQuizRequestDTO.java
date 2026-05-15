package com.university.dto.request.lecturer;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseQuizRequestDTO {
    @NotNull(message = "Exercise ID không được để trống")
    private UUID exerciseId;
}