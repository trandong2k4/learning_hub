package com.university.service.chatbot;

public class IntentDetector {

    public static ChatIntent detect(String message) {
        if (message == null || message.isBlank()) return ChatIntent.GENERAL;
        String lower = message.toLowerCase();

        if (anyOf(lower, "lịch học", "thời khóa biểu", "buổi học", "học lúc",
                "ngày học", "tiết học", "học hôm", "hôm nay học", "tuần này học",
                "học ngày", "học thứ"))
            return ChatIntent.SCHEDULE;

        if (anyOf(lower, "điểm", "bảng điểm", "kết quả học tập", "điểm số",
                "gpa", "điểm thi", "điểm thành phần", "điểm môn"))
            return ChatIntent.GRADES;

        if (anyOf(lower, "học phí", "đóng tiền", "thanh toán", "nợ học phí",
                "số tiền học", "hóa đơn", "chưa đóng", "đã đóng"))
            return ChatIntent.TUITION;

        if (anyOf(lower, "đăng ký tín chỉ", "môn đã đăng ký", "tín chỉ đã học",
                "học phần đăng ký", "môn học đăng ký", "đang học môn"))
            return ChatIntent.REGISTRATION;

        if (anyOf(lower, "bài tập", "quiz", "bài kiểm tra", "deadline",
                "hạn nộp", "nộp bài", "bài chưa nộp"))
            return ChatIntent.EXERCISE_QUIZ;

        if (anyOf(lower, "điểm danh", "vắng học", "chuyên cần", "tỷ lệ vắng",
                "số buổi vắng", "tình trạng điểm danh"))
            return ChatIntent.ATTENDANCE;

        if (anyOf(lower, "thông báo", "có gì mới", "thông tin mới", "tin tức",
                "thông báo mới", "chưa đọc"))
            return ChatIntent.NOTIFICATION;

        if (anyOf(lower, "thông tin cá nhân", "mssv", "mã sinh viên", "ngành học",
                "thông tin của tôi", "hồ sơ", "tên tôi là", "tôi là ai"))
            return ChatIntent.PROFILE;

        return ChatIntent.GENERAL;
    }

    private static boolean anyOf(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }
}
