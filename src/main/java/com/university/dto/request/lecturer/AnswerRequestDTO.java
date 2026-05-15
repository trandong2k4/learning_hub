package com.university.dto.request.lecturer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequestDTO {
    private String keyAnswers; // A, B, C, D
    private String conText;
    private Boolean isCorrect;
}
