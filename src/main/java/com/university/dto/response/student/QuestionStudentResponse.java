package com.university.dto.response.student;


import lombok.Data;
import java.util.List;
import java.util.UUID;
@Data

public class QuestionStudentResponse {
    private UUID id;
    private String content;

    private List<AnswerStudentResponse> answers;
}
