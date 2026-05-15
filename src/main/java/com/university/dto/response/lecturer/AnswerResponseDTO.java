package com.university.dto.response.lecturer;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDTO {
    private UUID answerId;
    private String keyAnswers;
    private String conText;
    private Boolean isCorrect;
}
