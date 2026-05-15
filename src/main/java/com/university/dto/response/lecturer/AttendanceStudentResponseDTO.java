package com.university.dto.response.lecturer;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStudentResponseDTO {
    private UUID hocVienId;
    private String hoTen;
    private String maHocVien;
    private String trangThai;
    private String ghiChu;
    private Integer soBuoiVang;
}
