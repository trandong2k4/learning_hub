package com.university.enums;

public enum CotDiemEnum {
    CHUYEN_CAN("Chuyên cần"),
    BAI_TAP("Bài tập"),
    THAO_LUAN("Thảo luận"),
    KIEM_TRA_15_PHUT("Kiểm tra 15 phút"),
    KIEM_TRA_1_TIET("Kiểm tra 1 tiết"),
    THUC_HANH("Thực hành"),
    THI_GIUA_KY("Thi giữa kỳ"),
    DO_AN("Đồ án"),
    TIEU_LUAN("Tiểu luận"),
    BAO_CAO("Báo cáo"),
    THI_CUOI_KY("Thi cuối kỳ"),
    THI_LAI("Thi lại");

    private final String tenHienThi;

    CotDiemEnum(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
