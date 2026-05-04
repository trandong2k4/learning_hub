package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardStudentResponse {

    private DashboardStudentProfileSummaryResponse thongTinCaNhan;
    private LichCaNhanStudentResponse lichHomNay;
    private TienDoHocTapTongQuanStudentResponse tienDoHocTap;
    private DashboardStudentNotificationSummaryResponse thongBao;
    private DashboardStudentTuitionSummaryResponse hocPhi;
}
