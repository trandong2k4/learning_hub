package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DashboardStudentProfileSummaryResponse {

    private UUID userId;
    private String hoTen;
    private String maHocVien;
    private String email;
    private String soDienThoai;
}
