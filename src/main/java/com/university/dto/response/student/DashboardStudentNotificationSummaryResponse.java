package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardStudentNotificationSummaryResponse {

    private Integer tongThongBao;
    private Integer soThongBaoChuaDoc;
    private List<DashboardStudentNotificationItemResponse> danhSachThongBao;
}
