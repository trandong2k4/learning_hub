package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HocPhiTongQuanStudentResponse {

    private Double tongCanThanhToan;
    private Double tongDaThanhToan;
    private Double tongQuaHan;
    private List<HocPhiStudentItemResponse> danhSachHocPhi;
}
