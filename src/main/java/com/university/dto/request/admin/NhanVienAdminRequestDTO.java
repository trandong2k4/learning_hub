package com.university.dto.request.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.university.enums.GioiTinhEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienAdminRequestDTO {

    // ---- NhanVien fields ----
    @NotBlank(message = "Mã nhân viên không được để trống")
    private String maNhanVien;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ngayNhanViec;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ngayNghiViec;

    // ---- User fields (optional for update) ----
    private String hoTen;
    private String username;
    private String passWord;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "^([0-9]{12})?$", message = "CCCD phải có đúng 12 chữ số")
    private String cccd;

    private String diaChi;

    @Pattern(
        regexp = "^(0[35789]\\d{8}|\\+84[35789]\\d{8}|\\+[1-9]\\d{8,13})?$",
        message = "Số điện thoại không hợp lệ (VD: 0912345678)"
    )
    private String soDienThoai;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    private GioiTinhEnum gioiTinh;
    private Boolean trangThai;
    private String ghiChu;
}
