package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LichSuThanhToanHocPhiStudentResponse {

    private List<HocPhiStudentItemResponse> lichSuThanhToan;
}
