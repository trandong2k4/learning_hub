package com.university.dto.response.accounting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaoCaoThongKeOverviewResponse {
    private double tongDoanhThu;
    private long soLuongThanhToan;
    private double tongCanThanhToan;
    private double tongDaThanhToan;
    private double tongQuaHan;
    private List<PaymentInfoResponse> danhSachThanhToanMoiNhat;
    private List<MonthlyRevenue> doanhThuTheoThang;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenue {
        private String namThang; // e.g. 2026-05
        private double doanhThu;
    }
}
