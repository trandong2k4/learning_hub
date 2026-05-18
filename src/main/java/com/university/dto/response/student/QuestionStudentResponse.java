package com.university.dto.response.student;


import lombok.Data;
import java.util.List;
import java.util.UUID;
@Data

public class QuestionStudentResponse {
    private UUID id;
    private String content;
    private Boolean loaiCauHoi;
    private Float diem;

    private List<AnswerStudentResponse> answers;
}
