package com.university.dto.response.admin;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminStatsResponseDTO {
    private long hocVienCount;
    private long hocVienDangHoc;
    private long hocVienTotNghiep;
    private long nganhCount;
    private long khoaCount;
    private long truongCount;
    private long monHocCount;
    private long baiVietCount;
    private long userCount;
    private long giangVienCount;
    private Map<String, Long> hocVienTheoNganh;
    private Map<Integer, Long> hocVienTheoNamNhapHoc;
}
