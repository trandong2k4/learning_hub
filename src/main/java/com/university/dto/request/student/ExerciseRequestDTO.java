package com.university.dto.request.student;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseRequestDTO {
    private UUID lopHocPhanId;
    @NotNull(message = "Id bài tập không được để trống")
    private UUID ExerciseId;

}
