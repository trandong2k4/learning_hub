package com.university.service.chatbot;

public enum ChatIntent {

    // ── Dùng chung cho mọi role ──────────────────────────────────────────
    GENERAL,        // câu hỏi chung, không xác định
    PROFILE,        // thông tin cá nhân, hồ sơ
    NOTIFICATION,   // thông báo hệ thống

    // ── Student ─────────────────────────────────────────────────────────
    SCHEDULE,       // lịch học, thời khóa biểu, buổi học
    GRADES,         // điểm số, GPA, kết quả học tập
    TUITION,        // học phí, thanh toán, công nợ sinh viên
    REGISTRATION,   // đăng ký tín chỉ, môn đã đăng ký
    EXERCISE_QUIZ,  // bài tập, quiz, deadline, hạn nộp
    ATTENDANCE,     // điểm danh, chuyên cần
    DOCUMENTS,      // tài liệu học tập

    // ── Lecturer ────────────────────────────────────────────────────────
    TEACHING_SCHEDULE,  // lịch dạy, buổi giảng dạy
    CLASSES,            // lớp phụ trách, danh sách sinh viên
    GRADING,            // chấm điểm, bài cần chấm
    ATTENDANCE_MGMT,    // quản lý điểm danh, danh sách vắng
    ASSIGNMENTS,        // quản lý bài tập
    QUIZ_MGMT,          // quản lý quiz, bài kiểm tra

    // ── Admin ────────────────────────────────────────────────────────────
    USER_STATS,     // thống kê người dùng, tài khoản hệ thống
    CLASS_MGMT,     // quản lý lớp học phần
    SEMESTER,       // học kỳ, kỳ học
    ROOM,           // phòng học, cơ sở vật chất
    ADMIN_SCHEDULE, // lịch hệ thống, thời khóa biểu tổng
    PERMISSION,     // phân quyền, vai trò, quyền hạn

    // ── Accountant ──────────────────────────────────────────────────────
    DEBT_OVERVIEW,      // công nợ học phí tổng hợp
    INVOICE,            // hóa đơn, chi tiết thu phí
    PAYMENT,            // lịch sử thanh toán gần đây
    FINANCIAL_REPORT    // báo cáo thu chi, thống kê tài chính
}
