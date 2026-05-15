package com.university.service.chatbot;

public enum ChatIntent {
    SCHEDULE,      // lịch học, thời khóa biểu
    GRADES,        // điểm, GPA, kết quả học tập
    TUITION,       // học phí, thanh toán
    REGISTRATION,  // đăng ký tín chỉ, môn đã đăng ký
    EXERCISE_QUIZ, // bài tập, quiz, deadline
    ATTENDANCE,    // điểm danh, chuyên cần
    NOTIFICATION,  // thông báo
    PROFILE,       // thông tin cá nhân, MSSV
    GENERAL        // câu hỏi chung
}
