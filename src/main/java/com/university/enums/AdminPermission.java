package com.university.enums;

/**
 * Quyen danh cho vai tro Quan tri vien (ADMIN)
 */
public enum AdminPermission {

    // === PERMISSIONS MANAGEMENT ===
    ADMIN_PERMISSIONS_VIEW("Xem danh sach quyen"),
    ADMIN_PERMISSIONS_CREATE("Tao quyen moi"),
    ADMIN_PERMISSIONS_UPDATE("Cap nhat quyen"),
    ADMIN_PERMISSIONS_DELETE("Xoa quyen"),

    // === ROLE MANAGEMENT ===
    ADMIN_ROLE_VIEW("Xem danh sach vai tro"),
    ADMIN_ROLE_CREATE("Tao vai tro moi"),
    ADMIN_ROLE_UPDATE("Cap nhat vai tro"),
    ADMIN_ROLE_DELETE("Xoa vai tro"),

    // === ACCOUNT MANAGEMENT ===
    ADMIN_ACCOUNT_VIEW("Xem danh sach tai khoan"),
    ADMIN_ACCOUNT_CREATE("Tao tai khoan moi"),
    ADMIN_ACCOUNT_UPDATE("Cap nhat tai khoan"),
    ADMIN_ACCOUNT_DELETE("Xoa tai khoan"),
    ADMIN_ACCOUNT_LOCK("Khoa tai khoan"),
    ADMIN_ACCOUNT_UNLOCK("Mo khoa tai khoan"),
    ADMIN_ACCOUNT_RESET_PASSWORD("Dat lai mat khau tai khoan"),

    // === STUDENT MANAGEMENT ===
    ADMIN_STUDENT_VIEW("Xem danh sach hoc vien"),
    ADMIN_STUDENT_CREATE("Tao hoc vien moi"),
    ADMIN_STUDENT_UPDATE("Cap nhat thong tin hoc vien"),
    ADMIN_STUDENT_DELETE("Xoa hoc vien"),
    ADMIN_STUDENT_PROFILE_VIEW("Xem ho so hoc vien"),
    ADMIN_STUDENT_PROFILE_UPDATE("Cap nhat ho so hoc vien"),

    // === STAFF MANAGEMENT ===
    ADMIN_STAFF_VIEW("Xem danh sach nhan vien"),
    ADMIN_STAFF_CREATE("Tao nhan vien moi"),
    ADMIN_STAFF_UPDATE("Cap nhat thong tin nhan vien"),
    ADMIN_STAFF_DELETE("Xoa nhan vien"),

    // === SCHOOL (Truong) MANAGEMENT ===
    ADMIN_SCHOOL_VIEW("Xem thong tin truong"),
    ADMIN_SCHOOL_CREATE("Tao thong tin truong"),
    ADMIN_SCHOOL_UPDATE("Cap nhat thong tin truong"),
    ADMIN_SCHOOL_DELETE("Xoa thong tin truong"),

    // === FACULTY (Khoa) MANAGEMENT ===
    ADMIN_KHOA_VIEW("Xem danh sach khoa"),
    ADMIN_KHOA_CREATE("Tao khoa moi"),
    ADMIN_KHOA_UPDATE("Cap nhat thong tin khoa"),
    ADMIN_KHOA_DELETE("Xoa khoa"),

    // === MAJOR (Nganh) MANAGEMENT ===
    ADMIN_NGANH_VIEW("Xem danh sach nganh"),
    ADMIN_NGANH_CREATE("Tao nganh moi"),
    ADMIN_NGANH_UPDATE("Cap nhat thong tin nganh"),
    ADMIN_NGANH_DELETE("Xoa nganh"),

    // === SUBJECT (Mon Hoc) MANAGEMENT ===
    ADMIN_SUBJECT_VIEW("Xem danh sach mon hoc"),
    ADMIN_SUBJECT_CREATE("Tao mon hoc moi"),
    ADMIN_SUBJECT_UPDATE("Cap nhat thong tin mon hoc"),
    ADMIN_SUBJECT_DELETE("Xoa mon hoc"),

    // === CLASS (Lop Hoc Phan) MANAGEMENT ===
    ADMIN_LOP_HOC_PHAN_MANAGE_VIEW("Xem danh sach lop hoc phan"),
    ADMIN_LOP_HOC_PHAN_CREATE("Tao lop hoc phan moi"),
    ADMIN_LOP_HOC_PHAN_UPDATE("Cap nhat thong tin lop hoc phan"),
    ADMIN_LOP_HOC_PHAN_DELETE("Xoa lop hoc phan"),

    // === ENROLLMENT (Dang Ky Tin Chi) MANAGEMENT ===
    ADMIN_ENROLLMENT_VIEW("Xem danh sach dang ky tin chi"),
    ADMIN_ENROLLMENT_CREATE("Tao moi dang ky tin chi"),
    ADMIN_ENROLLMENT_CANCEL("Huy dang ky tin chi"),
    ADMIN_ENROLLMENT_DELETE("Xoa dang ky tin chi"),

    // === SCHEDULE (Lich) MANAGEMENT ===
    ADMIN_SCHEDULE_MANAGE_VIEW("Xem quan ly lich hoc/giang day"),
    ADMIN_SCHEDULE_CREATE("Tao lich hoc/giang day moi"),
    ADMIN_SCHEDULE_UPDATE("Cap nhat lich hoc/giang day"),
    ADMIN_SCHEDULE_DELETE("Xoa lich hoc/giang day"),
    ADMIN_SCHEDULE_LEARNING_VIEW("Xem lich hoc cua hoc vien"),

    // === CONTACT / FEEDBACK MANAGEMENT ===
    ADMIN_CONTACT_VIEW("Xem danh sach lien he/phan hoi"),
    ADMIN_CONTACT_SEND("Gui phan hoi lien he"),
    ADMIN_CONTACT_MANAGE_MESSAGE("Quan ly tin nhan lien he"),

    // === NOTIFICATION MANAGEMENT ===
    ADMIN_NOTIFICATION_VIEW("Xem danh sach thong bao"),
    ADMIN_NOTIFICATION_CREATE("Tao thong bao moi"),
    ADMIN_NOTIFICATION_UPDATE("Cap nhat thong bao"),
    ADMIN_NOTIFICATION_DELETE("Xoa thong bao"),
    ADMIN_NOTIFICATION_SEND("Gui thong bao"),

    // === POST (Bai Viet) MANAGEMENT ===
    ADMIN_POST_VIEW("Xem danh sach bai viet"),
    ADMIN_POST_CREATE("Tao bai viet moi"),
    ADMIN_POST_UPDATE("Cap nhat bai viet"),
    ADMIN_POST_DELETE("Xoa bai viet"),

    // === TUITION (Hoc Phi) MANAGEMENT ===
    ADMIN_TUITION_VIEW("Xem danh sach hoc phi"),
    ADMIN_TUITION_CREATE("Tao hoc phi moi"),
    ADMIN_TUITION_UPDATE("Cap nhat hoc phi"),
    ADMIN_TUITION_DELETE("Xoa hoc phi"),
    ADMIN_TUITION_PAY("Xu ly thanh toan hoc phi"),

    // === DASHBOARD ===
    ADMIN_DASHBOARD_ADMIN_VIEW("Xem dashboard quan tri");

    private final String moTa;

    AdminPermission(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
