package com.university.service.chatbot;

/**
 * Keyword-based intent classifier. Role-aware: the same keyword may map to
 * a different intent depending on who is asking (e.g. "điểm danh" = student
 * ATTENDANCE vs lecturer ATTENDANCE_MGMT).
 */
public class IntentDetector {

    /** Detect intent for a student user. */
    public static ChatIntent detectForStudent(String message) {
        if (isBlank(message)) return ChatIntent.GENERAL;
        String m = message.toLowerCase();

        if (anyOf(m, "lịch học", "thời khóa biểu", "buổi học", "học lúc",
                "ngày học", "tiết học", "học hôm", "hôm nay học", "tuần này học",
                "học ngày", "học thứ"))
            return ChatIntent.SCHEDULE;

        if (anyOf(m, "điểm", "bảng điểm", "kết quả học tập", "điểm số",
                "gpa", "điểm thi", "điểm thành phần", "điểm môn"))
            return ChatIntent.GRADES;

        if (anyOf(m, "học phí", "đóng tiền", "thanh toán", "nợ học phí",
                "số tiền học", "hóa đơn", "chưa đóng", "đã đóng"))
            return ChatIntent.TUITION;

        if (anyOf(m, "đăng ký tín chỉ", "môn đã đăng ký", "tín chỉ đã học",
                "học phần đăng ký", "môn học đăng ký", "đang học môn"))
            return ChatIntent.REGISTRATION;

        if (anyOf(m, "bài tập", "quiz", "bài kiểm tra", "deadline",
                "hạn nộp", "nộp bài", "bài chưa nộp"))
            return ChatIntent.EXERCISE_QUIZ;

        if (anyOf(m, "điểm danh", "vắng học", "chuyên cần", "tỷ lệ vắng",
                "số buổi vắng", "tình trạng điểm danh"))
            return ChatIntent.ATTENDANCE;

        if (anyOf(m, "tài liệu", "slide", "giáo trình", "file môn học",
                "tài nguyên học tập"))
            return ChatIntent.DOCUMENTS;

        if (anyOf(m, "thông báo", "có gì mới", "thông tin mới", "tin tức",
                "thông báo mới", "chưa đọc"))
            return ChatIntent.NOTIFICATION;

        if (anyOf(m, "thông tin cá nhân", "mssv", "mã sinh viên", "ngành học",
                "thông tin của tôi", "hồ sơ", "tên tôi là", "tôi là ai"))
            return ChatIntent.PROFILE;

        return ChatIntent.GENERAL;
    }

    /** Detect intent for a lecturer user. */
    public static ChatIntent detectForLecturer(String message) {
        if (isBlank(message)) return ChatIntent.GENERAL;
        String m = message.toLowerCase();

        if (anyOf(m, "lịch dạy", "buổi giảng", "dạy hôm", "lịch giảng",
                "dạy lúc", "lịch giảng dạy", "hôm nay dạy"))
            return ChatIntent.TEACHING_SCHEDULE;

        if (anyOf(m, "lớp phụ trách", "lớp của tôi", "danh sách sinh viên",
                "lớp học phần", "sinh viên lớp", "danh sách lớp"))
            return ChatIntent.CLASSES;

        if (anyOf(m, "chấm điểm", "bài cần chấm", "chưa chấm", "điểm bài",
                "chấm bài", "nhập điểm", "cần chấm"))
            return ChatIntent.GRADING;

        if (anyOf(m, "điểm danh", "vắng", "danh sách vắng", "điểm danh hôm",
                "kiểm tra vắng", "chuyên cần lớp", "điểm danh sinh viên"))
            return ChatIntent.ATTENDANCE_MGMT;

        if (anyOf(m, "bài tập", "deadline bài tập", "bài cần giao",
                "bài đã giao", "hạn nộp bài", "bài chưa có hạn"))
            return ChatIntent.ASSIGNMENTS;

        if (anyOf(m, "quiz", "bài kiểm tra", "câu hỏi trắc nghiệm",
                "tạo quiz", "kết quả quiz"))
            return ChatIntent.QUIZ_MGMT;

        if (anyOf(m, "thông báo", "có gì mới", "thông tin mới", "chưa đọc"))
            return ChatIntent.NOTIFICATION;

        if (anyOf(m, "thông tin cá nhân", "hồ sơ", "mã giảng viên",
                "thông tin của tôi"))
            return ChatIntent.PROFILE;

        return ChatIntent.GENERAL;
    }

    /** Detect intent for an admin user. */
    public static ChatIntent detectForAdmin(String message) {
        if (isBlank(message)) return ChatIntent.GENERAL;
        String m = message.toLowerCase();

        if (anyOf(m, "thống kê người dùng", "tổng số user", "tài khoản hệ thống",
                "số lượng sinh viên", "số lượng giảng viên", "người dùng"))
            return ChatIntent.USER_STATS;

        if (anyOf(m, "lớp học phần", "danh sách lớp", "lớp đang mở",
                "quản lý lớp", "tổng số lớp"))
            return ChatIntent.CLASS_MGMT;

        if (anyOf(m, "học kỳ", "kỳ học", "năm học", "học kỳ hiện tại",
                "học kỳ đang chạy"))
            return ChatIntent.SEMESTER;

        if (anyOf(m, "phòng học", "phòng trống", "danh sách phòng",
                "cơ sở vật chất", "phòng máy"))
            return ChatIntent.ROOM;

        if (anyOf(m, "lịch hệ thống", "thời khóa biểu tổng", "lịch toàn trường",
                "lịch học phần"))
            return ChatIntent.ADMIN_SCHEDULE;

        if (anyOf(m, "phân quyền", "vai trò", "quyền hạn", "cấp quyền",
                "role", "permission"))
            return ChatIntent.PERMISSION;

        if (anyOf(m, "học phí", "thanh toán", "tổng thu", "tài chính"))
            return ChatIntent.DEBT_OVERVIEW;

        if (anyOf(m, "thông báo", "có gì mới", "chưa đọc"))
            return ChatIntent.NOTIFICATION;

        if (anyOf(m, "thông tin cá nhân", "hồ sơ", "tài khoản của tôi"))
            return ChatIntent.PROFILE;

        return ChatIntent.GENERAL;
    }

    /** Detect intent for an accountant user. */
    public static ChatIntent detectForAccountant(String message) {
        if (isBlank(message)) return ChatIntent.GENERAL;
        String m = message.toLowerCase();

        if (anyOf(m, "công nợ", "tổng nợ", "sinh viên nợ", "danh sách nợ",
                "chưa đóng học phí", "quá hạn học phí", "tổng thu"))
            return ChatIntent.DEBT_OVERVIEW;

        if (anyOf(m, "hóa đơn", "chi tiết học phí", "học phí học kỳ",
                "số tiền phải đóng", "thống kê học phí"))
            return ChatIntent.INVOICE;

        if (anyOf(m, "thanh toán", "đã thanh toán", "lịch sử thanh toán",
                "giao dịch gần đây", "thanh toán gần đây"))
            return ChatIntent.PAYMENT;

        if (anyOf(m, "báo cáo", "báo cáo tài chính", "thu chi", "doanh thu",
                "báo cáo thu", "thống kê thu"))
            return ChatIntent.FINANCIAL_REPORT;

        if (anyOf(m, "thông báo", "có gì mới", "chưa đọc"))
            return ChatIntent.NOTIFICATION;

        if (anyOf(m, "thông tin cá nhân", "hồ sơ"))
            return ChatIntent.PROFILE;

        return ChatIntent.GENERAL;
    }

    /** Dispatch to correct detector based on primary role. Falls back to student detector. */
    public static ChatIntent detect(String message, String primaryRole) {
        return switch (primaryRole) {
            case "LECTURER" -> detectForLecturer(message);
            case "ADMIN" -> detectForAdmin(message);
            case "ACCOUNTANT" -> detectForAccountant(message);
            default -> detectForStudent(message);
        };
    }

    /** Legacy single-arg overload — keeps student as default (backward compat). */
    public static ChatIntent detect(String message) {
        return detectForStudent(message);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static boolean anyOf(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }
}
