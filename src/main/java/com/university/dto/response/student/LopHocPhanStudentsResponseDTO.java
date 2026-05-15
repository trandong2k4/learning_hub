package com.university.dto.response.student;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LopHocPhanStudentsResponseDTO {

    private UUID id;
    private String maLopHocPhan;
    private Integer soLuongToiDa;
    private Long soLuongHienTai;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime hanDangKy;

    private UUID monHocId;
    private String maMonHoc;
    private String tenMonHoc;
    private Integer soTinChi;

    private UUID hocKiId;
    private String maHocKi;
    private String tenHocKi;
}
