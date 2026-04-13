package com.university.dto.response.student;


import java.time.LocalDateTime;
import java.util.UUID;

import com.university.enums.GioiTinhEnum;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class HocVienProfileResponseDTO {

    private UUID id;
    private String userName;
    private String hoTen;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private GioiTinhEnum gioiTinh;
    private LocalDateTime ngaySinh;
    private String cccd;
    private String maHocVien;
    private UUID nganhId;
    private LocalDateTime ngayNhapHoc;
    private LocalDateTime ngayTotNghiep;

    public HocVienProfileResponseDTO(
            UUID id,
            String userName,
            String hoTen,
            String diaChi,
            String soDienThoai,
            String email,
            GioiTinhEnum gioiTinh,
            LocalDateTime ngaySinh,
            String cccd,
            String maHocVien,
            UUID nganhId,
            LocalDateTime ngayNhapHoc,
            LocalDateTime ngayTotNghiep
    ) {
        this.id = id;
        this.userName = userName;
        this.hoTen = hoTen;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.cccd = cccd;
        this.maHocVien = maHocVien;
        this.nganhId = nganhId;
        this.ngayNhapHoc = ngayNhapHoc;
        this.ngayTotNghiep = ngayTotNghiep;
    }
}