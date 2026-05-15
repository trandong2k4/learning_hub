package com.university.dto.response.lecturer;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO {
    private UUID questionId;
    private String noiDung;
    private Boolean loaiCauHoi;
    private Boolean nhieuDapAn;
    private Float diem;
    private List<AnswerResponseDTO> answers;

    public QuestionResponseDTO(
            UUID questionId,
            String noiDung,
            Boolean loaiCauHoi,
            Float diem,
            List<AnswerResponseDTO> answers) {
        this(questionId, noiDung, loaiCauHoi, false, diem, answers);
    }
}
