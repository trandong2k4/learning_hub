package com.university.dto.response.student;

import lombok.Data;
import java.util.UUID;

@Data
public class AnswerStudentResponse {

    private UUID id;
    private String content;
}