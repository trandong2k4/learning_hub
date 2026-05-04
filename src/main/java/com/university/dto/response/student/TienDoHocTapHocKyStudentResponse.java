package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class TienDoHocTapHocKyStudentResponse {

    private UUID hocKiId;
    private String maHocKi;
    private String tenHocKi;
    private Integer tongTinChi;
    private Integer tinChiHoanThanh;
    private Double gpaHocKy;
    private Double phanTramHoanThanhHocKy;
    private List<TienDoHocTapMonHocStudentResponse> monHoc;
}
