package com.university.dto.response.admin;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponseDTO {

    // Thống kê tổng quan
    private long tongNguoiDung;
    private long tongGiangVien;
    private long tongLopHocPhan;
    private long tongHocVien;

    // Học phí
    private double tongHocPhi;
    private double hocPhiDaThu;
    private double hocPhiConNo;

    // Quick stats
    private long hocVienMoi;
    private long lopDangHoatDong;
    private long lienHeChuaXuLy;
    private long lienHeDangXuLy;
    private long lienHeDaXuLy;

    // Biểu đồ doanh thu theo tháng
    private List<DoanhThuThang> doanhThuTheoThang;

    // Biểu đồ tăng trưởng học viên theo năm
    private List<HocVienTheoNam> hocVienTheoNam;

    // Hoạt động gần đây
    private List<HoatDongGanDay> hoatDongGanDay;

    // Tổng hợp theo ngành
    private List<HocVienTheoNganh> hocVienTheoNganh;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoanhThuThang {
        private Integer thang;
        private Integer nam;
        private Long soLuong;
        private Double tongTien;
        private Double tienDaThu;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HocVienTheoNam {
        private Integer nam;
        private Long soHocVien;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HocVienTheoNganh {
        private String tenNganh;
        private Long soHocVien;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HoatDongGanDay {
        private String id;
        private String loai;
        private String tieuDe;
        private String moTa;
        private LocalDateTime thoiGian;
    }
}
