package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
public class LichCaNhanStudentItemResponse {

    private UUID lichId;
    private UUID lopHocPhanId;
    private String maLopHocPhan;
    private UUID monHocId;
    private String maMonHoc;
    private String tenMonHoc;
    private LocalDateTime ngayHoc;
    private LocalTime thoiGianBatDau;
    private LocalTime thoiGianKetThuc;
    private UUID phongId;
    private String maPhong;
    private String tenPhong;
    private String ghiChu;
}
