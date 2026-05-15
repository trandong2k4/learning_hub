package com.university.enums;

/**
 * Quyen danh cho vai tro Sinh vien (STUDENT)
 */
public enum StudentPermission {

    // === CURRICULUM ===
    STU_CURRICULUM_SEARCH("Tim kiem chuong trinh dao tao"),

    // === CREDIT REGISTRATION ===
    STU_CREDIT_REG_CREATE("Dang ky tin chi"),
    STU_CREDIT_REG_DELETE("Huy dang ky tin chi"),
    STU_CREDIT_REG_VIEW("Xem danh sach dang ky tin chi"),

    // === LECTURER REVIEW ===
    STU_LECTURER_REVIEW_VIEW("Xem danh gia giang vien"),
    STU_LECTURER_REVIEW_DRAFT("Luu nhung danh gia giang vien"),
    STU_LECTURER_REVIEW_SUBMIT("Gui danh gia giang vien"),

    // === EXERCISE ===
    STU_EXERCISE_LIST_VIEW("Xem danh sach bai tap"),
    STU_EXERCISE_DETAIL_VIEW("Xem chi tiet bai tap"),
    STU_EXERCISE_UPCOMING_VIEW("Xem bai tap sap toi"),
    STU_EXERCISE_OPEN_VIEW("Xem bai tap dang mo"),
    STU_EXERCISE_CLOSED_VIEW("Xem bai tap da dong"),
    STU_EXERCISE_OPEN_STATUS("Mo bai tap"),
    STU_EXERCISE_RESULT_STATUS("Xem ket qua bai tap"),
    STU_EXERCISE_EDIT_STATUS("Chinh sua trang thai bai tap"),
    STU_EXERCISE_SUBMIT("Nop bai tap"),

    // === TUITION ===
    STU_TUITION_SUMMARY_VIEW("Xem tong hop hoc phi"),
    STU_TUITION_METHOD_VIEW("Xem phuong thuc thanh toan hoc phi"),
    STU_TUITION_PAY_CREATE("Thanh toan hoc phi"),
    STU_TUITION_HISTORY_VIEW("Xem lich su thanh toan hoc phi"),
    STU_TUITION_INVOICE_DL("Tai hoa don hoc phi"),
    STU_TUITION_RECEIPT_DL("Tai phieu thu hoc phi"),

    // === PROFILE ===
    STU_PROFILE_VIEW("Xem thong tin ca nhan"),
    STU_PROFILE_UPDATE("Cap nhat thong tin ca nhan"),

    // === SCHEDULE ===
    STU_SCHEDULE_VIEW("Xem lich hoc"),

    // === QUIZ ===
    STU_QUIZ_LIST_VIEW("Xem danh sach bai kiem tra"),
    STU_QUIZ_OPEN_VIEW("Xem bai kiem tra dang mo"),
    STU_QUIZ_DETAIL_VIEW("Xem chi tiet bai kiem tra"),
    STU_QUIZ_SEARCH("Tim kiem bai kiem tra"),
    STU_QUIZ_START("Bat dau lam bai kiem tra"),
    STU_QUIZ_SUBMIT("Nop bai kiem tra"),

    // === DOCUMENT ===
    STU_DOC_LIST_VIEW("Xem danh sach tai lieu"),
    STU_DOC_SEARCH("Tim kiem tai lieu"),

    // === NOTIFICATION ===
    STU_NOTIFY_VIEW("Xem thong bao"),
    STU_NOTIFY_MARK_READ("Danh dau doc thong bao"),
    STU_NOTIFY_MARK_ALL_READ("Danh dau doc tat ca thong bao"),

    // === DASHBOARD ===
    STU_DASHBOARD_VIEW("Xem dashboard sinh vien"),
    STU_DASH_PROFILE_VIEW("Xem thong tin ca nhan tren dashboard"),
    STU_DASH_SCHEDULE_VIEW("Xem lich hoc tren dashboard"),
    STU_DASH_LEARN_PROGRESS("Xem tien do hoc tap tren dashboard"),
    STU_DASH_NOTIFY_VIEW("Xem thong bao tren dashboard"),
    STU_DASH_TUITION_VIEW("Xem hoc phi tren dashboard");

    private final String moTa;

    StudentPermission(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
