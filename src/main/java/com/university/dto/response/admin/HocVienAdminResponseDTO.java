package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.university.enums.GioiTinhEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HocVienAdminResponseDTO {

    private UUID id;
    private String maHocVien;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayNhapHoc;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayTotNghiep;
    private UUID nganhId;
    private String tenNganh;

    private String tenNhanVien;
    private String userName;
    private String email;
    private String cccd;
    private String diaChi;
    private String soDienThoai;
    private GioiTinhEnum gioiTinh;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngaySinh;

    private UUID usersId;
    private Boolean trangThai;
    private String ghiChu;

    public interface HocVienView {

        UUID getId();

        String getMaHocVien();

        LocalDateTime getNgayNhapHoc();

        LocalDateTime getNgayTotNghiep();

        NganhInfo getNganh();

        interface NganhInfo {
            UUID getId();
        }
    }
}
