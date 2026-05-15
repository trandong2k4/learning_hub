package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSubmissionResponseDTO {
    private UUID submissionId;
    private Integer phienThucHien;
    private String fileExerciseUrl;
    private LocalDateTime thoiGianNop;
    private Double diem;
    private String ghiChu;
    private UUID assignmentId;
    private String assignmentTitle;
    private UUID lopHocPhanId;
    private String maLopHocPhan;
    private String tenMonHoc;
    private UUID hocVienId;
    private String maHocVien;
    private String tenHocVien;
}
