package com.university.service.chatbot;

import com.university.dto.response.student.ThongBaoResponse;
import com.university.entity.*;
import com.university.repository.admin.DiemThanhPhanAdminRepository;
import com.university.repository.student.*;
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
 * Provides chat context for students (ROLE_student).
 * Moved from ChatbotQueryService — all student-specific DB logic lives here.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentChatContextProvider implements ChatContextProvider {

    private final HocVienStudentsRepository hocVienRepo;
    private final LichStudentRepository lichRepo;
    private final DiemThanhPhanAdminRepository diemRepo;
    private final HocPhiStudentRepository hocPhiRepo;
    private final DangKyTinChiRepository dangKyRepo;
    private final ThongBaoNguoiDungRepository thongBaoRepo;
    private final ExerciseStudentsRepository exerciseRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String SYSTEM_PROMPT = """
            Bạn là trợ lý học vụ thông minh dành cho sinh viên của LearningHub.
            Vai trò: hỗ trợ sinh viên tra cứu lịch học, điểm số, học phí, đăng ký tín chỉ và bài tập.

            Quy tắc bắt buộc:
            - Chỉ trả lời dựa trên dữ liệu được cung cấp trong phần [Dữ liệu hệ thống].
            - Không bịa hoặc suy đoán dữ liệu ngoài context được cấp.
            - Nếu không tìm thấy thông tin, hãy nói rõ và hướng dẫn sinh viên tra cứu trực tiếp trên hệ thống.
            - Không tiết lộ dữ liệu của sinh viên khác.
            - Trả lời ngắn gọn, dễ hiểu, lịch sự bằng tiếng Việt.
            """;

    @Override
    public boolean supports(CustomUserDetails userDetails) {
        return hasRole(userDetails, "STUDENT");
    }

    @Override
    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String buildContext(CustomUserDetails userDetails, String message, ChatIntent intent) {
        UUID userId = userDetails.getUserId();
        HocVien hv = hocVienRepo.findByUsersIdWithNganh(userId).orElse(null);
        if (hv == null) return "";

        UUID hocVienId = hv.getId();
        StringBuilder sb = new StringBuilder();
        appendProfile(sb, hv);

        switch (intent) {
            case SCHEDULE  -> appendSchedule(sb, hocVienId);
            case GRADES    -> appendGrades(sb, hocVienId);
            case TUITION   -> appendTuition(sb, hocVienId);
            case REGISTRATION -> appendRegistrations(sb, hocVienId);
            case EXERCISE_QUIZ -> appendExercises(sb, hocVienId);
            case NOTIFICATION  -> appendNotifications(sb, userId);
            case ATTENDANCE -> sb.append("\n[Để xem điểm danh chi tiết, vui lòng vào mục Điểm danh trong hệ thống.]\n");
            case PROFILE   -> { /* profile already appended above */ }
            default -> appendSchedule(sb, hocVienId);
        }

        return sb.toString();
    }

    // ── private helpers (unchanged from original ChatbotQueryService) ──────

    private void appendProfile(StringBuilder sb, HocVien hv) {
        sb.append("=== THÔNG TIN SINH VIÊN ===\n");
        sb.append("Họ tên: ").append(hv.getUsers().getHoTen()).append("\n");
        sb.append("MSSV: ").append(hv.getMaHocVien()).append("\n");
        sb.append("Ngành: ").append(hv.getNganh().getTenNganh()).append("\n");
        if (hv.getNgayNhapHoc() != null)
            sb.append("Ngày nhập học: ").append(hv.getNgayNhapHoc().format(DATE_FMT)).append("\n");
        sb.append("\n");
    }

    private void appendSchedule(StringBuilder sb, UUID hocVienId) {
        LocalDateTime now = LocalDateTime.now();
        List<Lich> lichs = lichRepo.findPersonalSchedule(hocVienId, now, now.plusDays(7));

        sb.append("=== LỊCH HỌC 7 NGÀY TỚI ===\n");
        if (lichs.isEmpty()) {
            sb.append("Không có lịch học trong 7 ngày tới.\n");
        } else {
            for (Lich l : lichs) {
                sb.append("- ").append(toDayOfWeekVN(l.getNgayHoc().getDayOfWeek()))
                        .append(", ").append(l.getNgayHoc().format(DATE_FMT))
                        .append(" (").append(l.getGioHoc().getThoiGianBatDau().format(TIME_FMT))
                        .append("–").append(l.getGioHoc().getThoiGianKetThuc().format(TIME_FMT)).append(")")
                        .append(": ").append(l.getLopHocPhan().getMonHoc().getTenMonHoc())
                        .append(" — Phòng ").append(l.getPhong().getMaPhong()).append("\n");
            }
        }
        sb.append("\n");
    }

    private void appendGrades(StringBuilder sb, UUID hocVienId) {
        List<DiemThanhPhan> diems = diemRepo.findAllByHocVienId(hocVienId);

        sb.append("=== BẢNG ĐIỂM ===\n");
        if (diems.isEmpty()) {
            sb.append("Chưa có điểm nào được ghi nhận.\n");
        } else {
            String currentMon = null;
            for (DiemThanhPhan d : diems) {
                String tenMon = d.getDangKyTinChi().getLopHocPhan().getMonHoc().getTenMonHoc();
                if (!tenMon.equals(currentMon)) {
                    sb.append("Môn: ").append(tenMon).append("\n");
                    currentMon = tenMon;
                }
                sb.append("  ").append(d.getCotDiem().getTenCotDiem())
                        .append(" (").append(d.getCotDiem().getTiTrong()).append("): ");
                if (d.getDiemSo() != null) sb.append(String.format("%.1f", d.getDiemSo()));
                else sb.append("Chưa có");
                sb.append("\n");
            }
        }
        sb.append("\n");
    }

    private void appendTuition(StringBuilder sb, UUID hocVienId) {
        List<HocPhi> hocPhis = hocPhiRepo.findAllByHocVienIdWithDetails(hocVienId);

        sb.append("=== HỌC PHÍ ===\n");
        if (hocPhis.isEmpty()) {
            sb.append("Không có thông tin học phí.\n");
        } else {
            for (HocPhi hp : hocPhis) {
                sb.append("- Học kỳ: ").append(hp.getHocKi().getTenHocKi())
                        .append(" | Số tiền: ").append(String.format("%,.0f VNĐ", hp.getSoTien()))
                        .append(" | Số tín chỉ: ").append(hp.getSoTinChi())
                        .append(" | Trạng thái: ").append(toTrangThaiVN(hp.getTrangThai())).append("\n");
            }
        }
        sb.append("\n");
    }

    private void appendRegistrations(StringBuilder sb, UUID hocVienId) {
        List<DangKyTinChi> dktcs = dangKyRepo.findAllByHocVienId(hocVienId);

        sb.append("=== MÔN HỌC ĐÃ ĐĂNG KÝ ===\n");
        if (dktcs.isEmpty()) {
            sb.append("Chưa đăng ký môn học nào.\n");
        } else {
            for (DangKyTinChi d : dktcs) {
                sb.append("- ").append(d.getLopHocPhan().getMonHoc().getTenMonHoc())
                        .append(" (").append(d.getLopHocPhan().getMonHoc().getSoTinChi()).append(" TC)")
                        .append(" — HK: ").append(d.getLopHocPhan().getHocKi().getTenHocKi()).append("\n");
            }
        }
        sb.append("\n");
    }

    private void appendExercises(StringBuilder sb, UUID hocVienId) {
        List<DangKyTinChi> dktcs = dangKyRepo.findAllByHocVienId(hocVienId);
        if (dktcs.isEmpty()) {
            sb.append("=== BÀI TẬP SẮP HẾT HẠN ===\nChưa đăng ký môn học nào.\n\n");
            return;
        }

        List<UUID> lopHocPhanIds = dktcs.stream()
                .map(d -> d.getLopHocPhan().getId())
                .collect(Collectors.toList());

        List<Exercise> exercises = exerciseRepository.findUpcomingByLopHocPhanIds(lopHocPhanIds, LocalDateTime.now());

        sb.append("=== BÀI TẬP SẮP HẾT HẠN ===\n");
        if (exercises.isEmpty()) {
            sb.append("Không có bài tập nào đang chờ nộp.\n");
        } else {
            for (Exercise e : exercises.stream().limit(10).collect(Collectors.toList())) {
                sb.append("- ").append(e.getTieuDe())
                        .append(" (").append(e.getLopHocPhan().getMonHoc().getTenMonHoc()).append(")")
                        .append(" — Hạn nộp: ").append(e.getThoiGianKetThuc().format(DATETIME_FMT)).append("\n");
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
                if (tb.getNoiDung() != null && !tb.getNoiDung().isBlank())
                    sb.append("  ").append(truncate(tb.getNoiDung(), 120)).append("\n");
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

    private String toTrangThaiVN(com.university.enums.HocPhiEnum trangThai) {
        if (trangThai == null) return "Không rõ";
        return switch (trangThai.name()) {
            case "DA_THANH_TOAN" -> "Đã thanh toán";
            case "CHUA_THANH_TOAN" -> "Chưa thanh toán";
            case "QUA_HAN" -> "Quá hạn";
            default -> trangThai.name();
        };
    }

    private String truncate(String text, int maxLen) {
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }
}
