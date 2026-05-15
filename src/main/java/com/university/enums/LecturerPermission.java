package com.university.enums;

/**
 * Quyền dành cho vai trò Giảng viên (LECTURE)
 */
public enum LecturerPermission {

    LECTURER_PROFILE("Quản lý thông tin cá nhân giảng viên"),
    LECTURER_TEACHING("Quản lý lớp học và lịch giảng dạy"),
    LECTURER_NOTIFICATION("Gửi thông báo cho sinh viên"),
    LECTURER_DOCUMENT("Quản lý tài liệu lớp học"),
    LECTURER_ASSESSMENT("Quản lý bài tập, điểm số và điểm danh");

    private final String moTa;

    LecturerPermission(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    /**
     * Kiểm tra xem một endpoint có yêu cầu quyền này không
     * @param method HTTP method (GET, POST, PUT, DELETE)
     * @param path endpoint path
     * @return quyền cần thiết hoặc null nếu không cần kiểm tra
     */
    public static LecturerPermission fromEndpoint(String method, String path) {
        // Profile
        if (path.contains("/profile") && !path.contains("/schedule")) {
            if ("PUT".equalsIgnoreCase(method)) return LECTURER_PROFILE;
            if ("GET".equalsIgnoreCase(method)) return LECTURER_PROFILE;
        }

        // Schedule
        if (path.contains("/schedule")) {
            return LECTURER_TEACHING;
        }

        // Dashboard
        if (path.contains("/dashboard")) {
            return LECTURER_TEACHING;
        }

        // Classes
        if (path.contains("/classes")) {
            return LECTURER_TEACHING;
        }

        // Notifications
        if (path.contains("/notifications")) {
            return LECTURER_NOTIFICATION;
        }

        // Documents
        if (path.contains("/documents")) {
            if ("POST".equalsIgnoreCase(method)) return LECTURER_DOCUMENT;
            if ("PUT".equalsIgnoreCase(method)) return LECTURER_DOCUMENT;
            if ("DELETE".equalsIgnoreCase(method)) return LECTURER_DOCUMENT;
            return LECTURER_DOCUMENT;
        }

        // Assignments
        if (path.contains("/assignments")) {
            if ("POST".equalsIgnoreCase(method)) return LECTURER_ASSESSMENT;
            if ("PUT".equalsIgnoreCase(method)) return LECTURER_ASSESSMENT;
            if ("DELETE".equalsIgnoreCase(method)) return LECTURER_ASSESSMENT;
            return LECTURER_ASSESSMENT;
        }

        // Grades
        if (path.contains("/grades")) {
            if ("PUT".equalsIgnoreCase(method)) return LECTURER_ASSESSMENT;
            return LECTURER_ASSESSMENT;
        }

        // Attendance
        if (path.contains("/attendance")) {
            if ("PUT".equalsIgnoreCase(method)) return LECTURER_ASSESSMENT;
            return LECTURER_ASSESSMENT;
        }

        return null;
    }
}