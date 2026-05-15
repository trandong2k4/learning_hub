package com.university.dto.response.lecturer;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeStudentResponseDTO {
    private UUID hocVienId;
    private String hoTen;
    private String maHocVien;
    private Float diemTrungBinh;
    private List<ComponentGradeEntryDTO> diemThanhPhan;
}
