package com.university.dto.request.lecturer;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequestDTO {
    private String noiDung;
    private Boolean loaiCauHoi; // true = trắc nghiệm, false = tự luận
    private Boolean nhieuDapAn; // true = trắc nghiệm nhiều đáp án, false = một đáp án
    private Float diem;
    private List<AnswerRequestDTO> answers;
}
