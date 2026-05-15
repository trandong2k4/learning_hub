package com.university.dto.response.lecturer;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizResultDTO {
    private UUID hocVienId;
    private String tenHocVien;
    private String maHocVien;
    private Float diem;
    private Integer soCauDung;
    private Integer tongCauHoi;
    private Integer usedTime; // thời gian đã dùng (giây)
    private Integer remainingTime; // thời gian còn lại (giây)
    private String status; // "COMPLETED", "IN_PROGRESS"
    private Boolean daTuCham; // đã được tự động chấm điểm (MCQ)
}