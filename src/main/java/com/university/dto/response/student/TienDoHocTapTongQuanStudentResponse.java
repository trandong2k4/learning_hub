package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TienDoHocTapTongQuanStudentResponse {

    private Integer tongSoMonTrongChuongTrinh;
    private Integer tongTinChiChuongTrinh;
    private Integer soMonDaHoc;
    private Integer soMonChuaHoc;
    private Integer soTinChiDaHoanThanh;
    private Double gpaTichLuy;
    private Double phanTramHoanThanh;
}
