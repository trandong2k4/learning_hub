package com.university.dto.request.admin;

import com.alibaba.excel.annotation.ExcelProperty;
import com.university.dto.request.admin.warrap.LocalTimeConverter;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GioHocAdminRequestDTO {

    @NotBlank(message = "Mã giờ học không được để trống")
    @ExcelProperty(index = 0)
    private String maGioHoc;

    @NotBlank(message = "Tên giờ học không được để trống")
    @ExcelProperty(index = 1)
    private String tenGioHoc;

    @ExcelProperty(index = 2, converter = LocalTimeConverter.class)
    private LocalTime thoiGianBatDau;
    @ExcelProperty(index = 3, converter = LocalTimeConverter.class)
    private LocalTime thoiGianKetThuc;
}
