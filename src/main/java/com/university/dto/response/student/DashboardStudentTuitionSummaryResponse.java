package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardStudentTuitionSummaryResponse {

    private Double tongCanThanhToan;
    private Double tongDaThanhToan;
    private Double tongQuaHan;
}
