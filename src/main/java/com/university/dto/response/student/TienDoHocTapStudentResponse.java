package com.university.dto.response.student;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TienDoHocTapStudentResponse {

    private Integer tongSoMonTrongChuongTrinh;
    private Integer tongTinChiChuongTrinh;
    private Integer soMonDaHoc;
    private Integer soMonChuaHoc;
    private Integer soTinChiDaHoanThanh;
    private Double gpaTichLuy;
    private Double phanTramHoanThanh;
    private List<TienDoHocTapMonHocStudentResponse> monDaHoc;
    private List<TienDoHocTapMonHocStudentResponse> monChuaHoc;
    private List<TienDoHocTapHocKyStudentResponse> tienDoTheoHocKy;
}
