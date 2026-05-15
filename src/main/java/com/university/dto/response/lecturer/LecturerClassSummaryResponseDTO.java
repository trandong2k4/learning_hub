package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerClassSummaryResponseDTO {
    private UUID lopHocPhanId;
    private String maLopHocPhan;
    private String tenMonHoc;
    private String phong;
    private String toaNha;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
}
