package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponseDTO {
    private UUID quizId;
    private String tieuDe;
    private Integer thoiGianLam;
    private Integer soLanLam;
    private Boolean trinhTrang;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private Integer tongSoHocVien;
    private Integer soLuongDaLam;
    private Float diemTrungBinh;
    private Integer tongCauHoi;
    private List<StudentQuizResultDTO> studentResults;
}
