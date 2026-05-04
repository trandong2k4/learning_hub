package com.university.dto.response.student;

import com.university.enums.HocPhiEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class HocPhiStudentItemResponse {

    private UUID hocPhiId;
    private UUID hocKiId;
    private String maHocKi;
    private String tenHocKi;
    private Double soTien;
    private Integer soTinChi;
    private HocPhiEnum trangThai;
    private LocalDateTime ngayThanhToan;
    private String fileChungTu;
    private String phuongThucThanhToan;
    private boolean daThanhToan;
    private String taiLieuTaiVe;
}
