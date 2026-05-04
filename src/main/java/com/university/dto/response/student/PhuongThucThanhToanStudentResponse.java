package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PhuongThucThanhToanStudentResponse {

    private String maPhuongThuc;
    private String tenPhuongThuc;
    private String moTa;
}
