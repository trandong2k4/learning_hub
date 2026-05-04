package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TienDoHocTapMonHocStudentResponse {

    private UUID monHocId;
    private UUID hocKiId;
    private String maMonHoc;
    private String tenMonHoc;
    private Integer soTinChi;
    private String maHocKi;
    private String tenHocKi;
    private Double diemTongKet;
    private Double diemHe4;
    private boolean daHoanThanh;
    private boolean daDat;
}
