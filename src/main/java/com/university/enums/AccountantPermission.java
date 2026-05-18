package com.university.enums;

/**
 * Quyen danh cho vai tro Ke toan vien (ACCOUNTANT)
 */
public enum AccountantPermission {

    // === TUITION ===
    ACCOUNTANT_TUITION_VIEW("Xem danh sach hoc phi"),
    ACCOUNTANT_TUITION_CREATE("Tao hoa don hoc phi"),
    ACCOUNTANT_TUITION_NOTIFY("Gui thong bao hoc phi"),
    ACCOUNTANT_TUITION_PAY("Xu ly thanh toan hoc phi"),

    // === REPORT ===
    ACCOUNTANT_REPORT_VIEW("Xem bao cao thong ke"),

    // === PROFILE ===
    ACCOUNTANT_PROFILE_VIEW("Xem thong tin ca nhan"),
    ACCOUNTANT_PROFILE_UPDATE("Cap nhat thong tin ca nhan");

    private final String moTa;

    AccountantPermission(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
