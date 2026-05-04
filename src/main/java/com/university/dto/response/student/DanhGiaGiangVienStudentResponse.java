package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DanhGiaGiangVienStudentResponse {

    private UUID lopHocPhanId;
    private String maLopHocPhan;
    private String tenMonHoc;
    private UUID nhanVienId;
    private String maNhanVien;
    private String tenGiangVien;
    private Integer diemDanhGia;
    private String nhanXet;
    private boolean daGui;
    private boolean coTheDanhGia;
    private LocalDateTime thoiGianMoDanhGia;
    private LocalDateTime thoiGianDongDanhGia;
}
