package com.university.dto.response.admin;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GiangDayAdminResponseDTO {

    private UUID id;
    private String vaiTro;

    private UUID nhanVienId;
    private String maNhanVien;
    private String tenNhanVien;

    private UUID lopHocPhanId;
    private String maLopHocPhan;
    private String tenMonHoc;
    private Integer soTinChi;

    private UUID hocKiId;
    private String maHocKi;
    private String tenHocKi;
}