package com.university.dto.response.student;

import lombok.Data;
import java.util.UUID;

@Data
public class QuizResultStudentResponse {

    private UUID attemptId;
    private Float score;
    private Integer correct;
    private Integer total;
}
    


