package com.university.dto.request.admin;

import com.alibaba.excel.annotation.ExcelProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocVienExcelDTO {

    @NotBlank(message = "Mã học viên không được để trống")
    @ExcelProperty(index = 0)
    private String maHocVien;

    @ExcelProperty(index = 1)
    private String hoTen;

    @ExcelProperty(index = 2)
    private String gioiTinh;

    @NotBlank(message = "CCCD không được để trống")
    @ExcelProperty(index = 3)
    private String cccd;

    @ExcelProperty(index = 4)
    private String email;

    @ExcelProperty(index = 5)
    private String diaChi;

    @ExcelProperty(index = 6)
    private String soDienThoai;

    @ExcelProperty(index = 7)
    private String ngaySinh;

    @ExcelProperty(index = 8)
    private String ngayNhapHoc;

    @ExcelProperty(index = 9)
    private String ngayTotNghiep;

    @NotBlank(message = "Mã ngành không được để trống")
    @ExcelProperty(index = 10)
    private String maNganh;
}
