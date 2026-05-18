package com.university.dto.request.admin;

import com.alibaba.excel.annotation.ExcelProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienExcelDTO {

    @NotBlank(message = "Mã nhân viên không được để trống")
    @ExcelProperty(index = 0)
    private String maNhanVien;

    @ExcelProperty(index = 1)
    private String ngayNhanViec;

    @ExcelProperty(index = 2)
    private String ngayNghiViec;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @ExcelProperty(index = 3)
    private String username;

    @ExcelProperty(index = 4)
    private String email;

    @NotBlank(message = "CCCD không được để trống")
    @ExcelProperty(index = 5)
    private String cccd;

    @ExcelProperty(index = 6)
    private String diaChi;

    @ExcelProperty(index = 7)
    private String soDienThoai;

    @ExcelProperty(index = 8)
    private String ngaySinh;

    @ExcelProperty(index = 9)
    private String gioiTinh;

    @ExcelProperty(index = 10)
    private String maRole;
}
