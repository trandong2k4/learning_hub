package com.university.dto.response.lecturer;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerClassDetailResponseDTO {
    private UUID lopHocPhanId;
    private String maLopHocPhan;
    private String tenMonHoc;
    private String phong;
    private String toaNha;
    private String lichMoTa;
    private List<LecturerClassStudentResponseDTO> hocViens;
}
