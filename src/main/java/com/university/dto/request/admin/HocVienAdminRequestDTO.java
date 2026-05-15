package com.university.dto.request.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocVienAdminRequestDTO {

    @NotBlank(message = "Mã học viên không được để trống")
    private String maHocVien;

    @NotBlank(message = "Mã ngành không được để trống")
    private String maNganh;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ngayNhapHoc;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ngayTotNghiep;

    // User fields (optional for update)
    private String hoTen;
    private String username;
    private String passWord;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "^([0-9]{12})?$", message = "CCCD phải có đúng 12 chữ số")
    private String cccd;

    private String diaChi;

    @Pattern(regexp = "^(0[35789]\\d{8}|02\\d{9})?$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    private GioiTinhEnum gioiTinh;
    private Boolean trangThai;
    private String ghiChu;
}
