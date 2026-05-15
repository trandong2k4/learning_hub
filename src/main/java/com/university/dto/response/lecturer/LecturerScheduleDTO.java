package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerScheduleDTO {
    private UUID lichId;
    private UUID lopHocPhanId;
    private String maLopHocPhan;
    private String tenMonHoc;
    private LocalDateTime ngayHoc;
    private String gioBatDau;
    private String gioKetThuc;
    private String phong;
    private String toaNha;
}
