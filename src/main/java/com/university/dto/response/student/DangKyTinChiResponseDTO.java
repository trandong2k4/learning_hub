package com.university.dto.response.student;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DangKyTinChiResponseDTO {

    private UUID id;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    private UUID hocVienId;
    private String maHocVien;

    private UUID lopHocPhanId;
    private String maLopHocPhan;

    private UUID monHocId;
    private String maMonHoc;
    private Integer soTinChi;

    private UUID hocKiId;
}
