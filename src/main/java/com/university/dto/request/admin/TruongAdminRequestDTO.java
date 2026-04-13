package com.university.dto.request.admin;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TruongAdminRequestDTO {

    @NotBlank(message = "Mã trường không được để trống")
    @Length(max = 10, message = "Mã trường tối đa 10 kí")
    @ExcelProperty(index = 0)
    private String maTruong;

    @ExcelProperty(index = 1)
    private String tenTruong;

    @ExcelProperty(index = 2)
    private String diaChi;

    @ExcelProperty(index = 3)
    private String moTa;

    @ExcelProperty(index = 4)
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDate ngayThanhLap;

    @ExcelProperty(index = 5)
    private String nguoiDaiDien;
}