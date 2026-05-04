package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class LichCaNhanStudentResponse {

    private String cheDoXem;
    private LocalDate ngayThamChieu;
    private LocalDate tuNgay;
    private LocalDate denNgay;
    private int tongSuKien;
    private List<LichCaNhanStudentItemResponse> lich;
}
