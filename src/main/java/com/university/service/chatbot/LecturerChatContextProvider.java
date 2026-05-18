package com.university.service.chatbot;

import com.university.dto.response.student.ThongBaoResponse;
import com.university.entity.Exercise;
import com.university.entity.Lich;
import com.university.entity.NhanVien;
import com.university.repository.lecturer.LecturerExerciseRepository;
import com.university.repository.lecturer.LecturerRepository;
import com.university.repository.lecturer.LecturerScheduleRepository;
import com.university.repository.student.ThongBaoNguoiDungRepository;
import com.university.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Provides chat context for lecturers / instructors (ROLE_instructor).
 */
@Component
@Order(2)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LecturerChatContextProvider implements ChatContextProvider {

    private final LecturerRepository lecturerRepo;
    private final LecturerScheduleRepository scheduleRepo;
    private final LecturerExerciseRepository exerciseRepo;
    private final ThongBaoNguoiDungRepository thongBaoRepo;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String SYSTEM_PROMPT = """
            Bạn là trợ lý giảng dạy thông minh dành cho giảng viên của LearningHub.
            Vai trò: hỗ trợ giảng viên xem lịch dạy, quản lý lớp học, bài tập cần chấm và thông báo.

            Quy tắc bắt buộc:
            - Chỉ trả lời dựa trên dữ liệu được cung cấp trong phần [Dữ liệu hệ thống].
            - Không bịa hoặc suy đoán thông tin ngoài context.
            - Nếu không tìm thấy dữ liệu, hãy nói rõ và hướng dẫn truy cập trực tiếp trên hệ thống.
            - Không tiết lộ dữ liệu của giảng viên hoặc sinh viên khác.
            - Trả lời ngắn gọn, chuyên nghiệp, lịch sự bằng tiếng Việt.
            """;

    @Override
    public boolean supports(CustomUserDetails userDetails) {
        return hasRole(userDetails, "LECTURER");
    }

    @Override
    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String buildContext(CustomUserDetails userDetails, String message, ChatIntent intent) {
        UUID userId = userDetails.getUserId();
        NhanVien nv = lecturerRepo.findByUsers_Id(userId).orElse(null);

        StringBuilder sb = new StringBuilder();

        if (nv != null) appendProfile(sb, nv);

        switch (intent) {
            case TEACHING_SCHEDULE -> appendTeachingSchedule(sb, userId);
            case CLASSES           -> appendTeachingSchedule(sb, userId);
            case GRADING           -> appendExercisesToGrade(sb, userId);
            case ASSIGNMENTS       -> appendExercisesToGrade(sb, userId);
            case NOTIFICATION      -> appendNotifications(sb, userId);
            case PROFILE           -> { /* profile already appended */ }
            case GENERAL           -> appendTeachingSchedule(sb, userId);
            default                -> appendTeachingSchedule(sb, userId);
        }

        return sb.toString();
    }

    // ── private helpers ───────────────────────────────────────────────────

    private void appendProfile(StringBuilder sb, NhanVien nv) {
        sb.append("=== THÔNG TIN GIẢNG VIÊN ===\n");
        sb.append("Họ tên: ").append(nv.getUsers().getHoTen()).append("\n");
        if (nv.getMaNhanVien() != null)
            sb.append("Mã GV: ").append(nv.getMaNhanVien()).append("\n");
        sb.append("\n");
    }

    private void appendTeachingSchedule(StringBuilder sb, UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Lich> lichs = scheduleRepo
                .findByLopHocPhan_DGiangDays_NhanVien_Users_IdAndNgayHocBetween(userId, now, now.plusDays(7));

        sb.append("=== LỊCH DẠY 7 NGÀY TỚI ===\n");
        if (lichs.isEmpty()) {
            sb.append("Không có lịch dạy trong 7 ngày tới.\n");
        } else {
            for (Lich l : lichs) {
                sb.append("- ").append(toDayOfWeekVN(l.getNgayHoc().getDayOfWeek()))
                        .append(", ").append(l.getNgayHoc().format(DATE_FMT))
                        .append(" (").append(l.getGioHoc().getThoiGianBatDau().format(TIME_FMT))
                        .append("–").append(l.getGioHoc().getThoiGianKetThuc().format(TIME_FMT)).append(")")
                        .append(": ").append(l.getLopHocPhan().getMonHoc().getTenMonHoc())
                        .append(" — Lớp ").append(l.getLopHocPhan().getMaLopHocPhan())
                        .append(" — Phòng ").append(l.getPhong().getMaPhong()).append("\n");
            }
        }
        sb.append("\n");
    }

    private void appendExercisesToGrade(StringBuilder sb, UUID userId) {
        List<Exercise> exercises = exerciseRepo
                .findByLopHocPhan_DGiangDays_NhanVien_Users_Id(userId);

        sb.append("=== BÀI TẬP GIẢNG VIÊN PHỤ TRÁCH ===\n");
        if (exercises.isEmpty()) {
            sb.append("Không có bài tập nào.\n");
        } else {
            // Show up to 10 most recent exercises
            for (Exercise e : exercises.stream().limit(10).collect(Collectors.toList())) {
                sb.append("- ").append(e.getTieuDe())
                        .append(" (").append(e.getLopHocPhan().getMonHoc().getTenMonHoc()).append(")")
                        .append(" — Hạn: ").append(
                                e.getThoiGianKetThuc() != null
                                        ? e.getThoiGianKetThuc().format(DATETIME_FMT)
                                        : "Chưa đặt")
                        .append("\n");
            }
        }
        sb.append("\n");
    }

    private void appendNotifications(StringBuilder sb, UUID userId) {
        List<ThongBaoResponse> tbList = thongBaoRepo.findThongBaoByUsersId(userId);

        sb.append("=== THÔNG BÁO MỚI NHẤT ===\n");
        if (tbList.isEmpty()) {
            sb.append("Không có thông báo nào.\n");
        } else {
            for (ThongBaoResponse tb : tbList.stream().limit(5).collect(Collectors.toList())) {
                sb.append("- [").append(Boolean.TRUE.equals(tb.getDaNhan()) ? "Đã đọc" : "Chưa đọc").append("] ")
                        .append(tb.getTieuDe())
                        .append(" (").append(tb.getCreatedAt().format(DATE_FMT)).append(")\n");
            }
        }
        sb.append("\n");
    }

    // ── utils ─────────────────────────────────────────────────────────────

    private boolean hasRole(CustomUserDetails u, String role) {
        return u.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_" + role));
    }

    private String toDayOfWeekVN(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> "Thứ 2";
            case TUESDAY -> "Thứ 3";
            case WEDNESDAY -> "Thứ 4";
            case THURSDAY -> "Thứ 5";
            case FRIDAY -> "Thứ 6";
            case SATURDAY -> "Thứ 7";
            case SUNDAY -> "Chủ nhật";
        };
    }
}
