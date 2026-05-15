package com.university.dto.request.lecturer;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AssignmentExcelRowDTO {
    @ExcelProperty(index = 0)
    private String tieuDe;

    @ExcelProperty(index = 1)
    private String moTa;

    @ExcelProperty(index = 2)
    private String fileExerciseUrl;

    @ExcelProperty(index = 3)
    private String thoiGianBatDau;

    @ExcelProperty(index = 4)
    private String thoiGianKetThuc;

    @ExcelProperty(index = 5)
    private String noiDungCauHoi;

    @ExcelProperty(index = 6)
    private String loaiCauHoi;

    @ExcelProperty(index = 7)
    private String diem;

    @ExcelProperty(index = 8)
    private String dapAnA;

    @ExcelProperty(index = 9)
    private String dapAnB;

    @ExcelProperty(index = 10)
    private String dapAnC;

    @ExcelProperty(index = 11)
    private String dapAnD;

    @ExcelProperty(index = 12)
    private String dapAnE;

    @ExcelProperty(index = 13)
    private String dapAnF;

    @ExcelProperty(index = 14)
    private String dapAnDung;

    @ExcelProperty(index = 15)
    private String gioiHanLanLam;
}
