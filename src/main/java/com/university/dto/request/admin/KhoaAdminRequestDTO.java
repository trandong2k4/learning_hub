package com.university.dto.request.admin;

import com.alibaba.excel.annotation.ExcelProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhoaAdminRequestDTO {

    @NotBlank(message = "Mã khoa không được để trống")
    @ExcelProperty(index = 0)
    private String maKhoa;

    @NotBlank(message = "Tên khoa không được để trống")
    @ExcelProperty(index = 1)
    private String tenKhoa;

    @ExcelProperty(index = 2)
    private String diaChi;

    @ExcelProperty(index = 3)
    private String moTa;

    @NotNull(message = "Mã trường không được để trống")
    @ExcelProperty(index = 4)
    private String maTruong;

}