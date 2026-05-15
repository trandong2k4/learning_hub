package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerDashboardResponseDTO {
    private List<LecturerScheduleDTO> todaySchedule;
    private List<LecturerScheduleDTO> weekSchedule;
    private List<LecturerScheduleDTO> monthSchedule;
    private int totalClasses;
    private int ungradedAssignments;
    private double attendanceRate;
    private int totalStudents;
    private int pendingQuizAttempts;
    private int totalPendingWork;
    private List<DashboardClassDTO> teachingClasses;
    private List<DashboardNotificationDTO> recentNotifications;
    private List<DashboardWorkItemDTO> pendingWorkItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardClassDTO {
        private UUID lopHocPhanId;
        private String maLopHocPhan;
        private String tenMonHoc;
        private String phong;
        private String toaNha;
        private LocalDateTime ngayBatDau;
        private LocalDateTime ngayKetThuc;
        private int studentCount;
        private int scheduleCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardNotificationDTO {
        private UUID id;
        private String tieuDe;
        private String noiDung;
        private String loaiThongBao;
        private LocalDateTime createdAt;
        private Boolean daNhan;
        private String source;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardWorkItemDTO {
        private String id;
        private String type;
        private String title;
        private String classCode;
        private String className;
        private String studentName;
        private LocalDateTime createdAt;
        private String actionPath;
    }
}
