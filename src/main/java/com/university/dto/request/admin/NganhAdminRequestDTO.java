package com.university.dto.request.admin;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NganhAdminRequestDTO {

    @ExcelProperty("maNganh")
    @NotBlank(message = "Mã ngành không được để trống")
    @Size(max = 10, message = "Mã ngành tối đa 10 ký tự")
    private String maNganh;

    @ExcelProperty("tenNganh")
    @NotBlank(message = "Tên ngành không được để trống")
    @Size(max = 255, message = "Tên ngành tối đa 255 ký tự")
    private String tenNganh;

    @ExcelProperty("moTa")
    private String moTa;

    @ExcelProperty("danhGia")
    private String danhGia;

    @ExcelProperty("maKhoa")
    @NotNull(message = "Mã khoa không được để trống")
    private String maKhoa;

}
