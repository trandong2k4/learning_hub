package com.university.dto.response.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NhanVienAdminResponseDTO {

    private UUID id;
    private String maNhanVien;
    private String tenNhanVien;
    private GioiTinhEnum gioiTinh;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayNhanViec;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayNghiViec;
    private UUID usersId;

    private String userName;
    private String email;
    private String cccd;
    private String diaChi;
    private String soDienThoai;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ngaySinh;
    private Boolean trangThai;
    private String ghiChu;

    public NhanVienAdminResponseDTO(
            UUID id,
            String maNhanVien,
            String tenNhanVien,
            GioiTinhEnum gioiTinh,
            LocalDateTime ngayNhanViec,
            LocalDateTime ngayNghiViec,
            UUID usersId,
            String userName,
            String email,
            String cccd,
            String diaChi,
            String soDienThoai,
            LocalDateTime ngaySinh,
            Boolean trangThai,
            String ghiChu) {
        this.id = id;
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.gioiTinh = gioiTinh;
        this.ngayNhanViec = ngayNhanViec;
        this.ngayNghiViec = ngayNghiViec;
        this.usersId = usersId;
        this.userName = userName;
        this.email = email;
        this.cccd = cccd;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.ngaySinh = ngaySinh != null ? ngaySinh.toLocalDate() : null;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public interface NhanVienView {
        UUID getId();
    }

}
