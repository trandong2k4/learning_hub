package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ThanhToanHocPhiStudentResponse {

    private UUID hocPhiId;
    private UUID thanhToanId;
    private String trangThaiGiaoDich;
    private String thongDiep;
    private String phuongThucThanhToan;
    private LocalDateTime ngayThanhToan;
    private String fileChungTu;
    private String taiBienLaiUrl;
}
