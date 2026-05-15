package com.university.dto.response.lecturer;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO {
    private UUID lopHocPhanId;
    private String selectedLichId;
    private List<SessionDTO> sessions;
    private List<AttendanceStudentResponseDTO> students;
    private AttendanceStatsDTO statistics;
    private Boolean canTakeAttendance;
    private String message;
    private String serverDateTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionDTO {
        private String lichId;
        private String ngayHoc;
        private LocalTime gioBatDau;
        private LocalTime gioKetThuc;
        private String phong;
        private Boolean currentSession;
        private Boolean canTakeAttendance;
        private Boolean hasAttendance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceStatsDTO {
        private int totalStudents;
        private int presentCount;
        private int absentCount;
        private int lateCount;
        private int excusedCount;
        private int pendingCount;
        private double attendanceRate;
    }
}
