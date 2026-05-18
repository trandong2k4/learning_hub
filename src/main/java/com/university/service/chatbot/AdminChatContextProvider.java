package com.university.service.chatbot;

import com.university.dto.response.admin.HocPhiAdminResponseDTO;
import com.university.dto.response.student.ThongBaoResponse;
import com.university.repository.admin.*;
import com.university.repository.student.ThongBaoNguoiDungRepository;
import com.university.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Provides chat context for system administrators (ROLE_admin).
 * Only returns aggregate/statistical data — never exposes raw personal records.
 */
@Component
@Order(4)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminChatContextProvider implements ChatContextProvider {

    private final UsersAdminRepository usersRepo;
    private final LopHocPhanAdminRepository lopHocPhanRepo;
    private final HocKiAdminRepository hocKiRepo;
    private final HocPhiAdminRepository hocPhiRepo;
    private final ThongBaoNguoiDungRepository thongBaoRepo;

    private static final String SYSTEM_PROMPT = """
            Bạn là trợ lý quản trị hệ thống của LearningHub dành cho quản trị viên.
            Vai trò: hỗ trợ admin tra cứu thống kê hệ thống, lớp học phần, học kỳ, phòng học và tài chính tổng quan.

            Quy tắc bắt buộc:
            - Chỉ trả lời dựa trên dữ liệu được cung cấp trong phần [Dữ liệu hệ thống].
            - Không bịa hoặc suy đoán dữ liệu.
            - Không tiết lộ thông tin cá nhân (mật khẩu, email, CCCD) của người dùng.
            - Khi trả lời số liệu tài chính, chú thích rõ đây là số liệu tổng hợp, không phải thông tin cá nhân.
            - Với các câu hỏi cần tra cứu sâu hơn, hướng dẫn truy cập module tương ứng trên hệ thống.
            - Trả lời ngắn gọn, chuyên nghiệp, bằng tiếng Việt.
            """;

    @Override
    public boolean supports(CustomUserDetails userDetails) {
        return hasRole(userDetails, "ADMIN");
    }

    @Override
    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    @Override
    public String buildContext(CustomUserDetails userDetails, String message, ChatIntent intent) {
        UUID userId = userDetails.getUserId();
        StringBuilder sb = new StringBuilder();

        switch (intent) {
            case USER_STATS              -> appendUserStats(sb);
            case CLASS_MGMT             -> appendClassStats(sb);
            case SEMESTER               -> appendSemesterStats(sb);
            case DEBT_OVERVIEW, INVOICE -> appendFinancialOverview(sb);
            case NOTIFICATION           -> appendNotifications(sb, userId);
            case GENERAL                -> {
                appendUserStats(sb);
                appendClassStats(sb);
                appendFinancialOverview(sb);
            }
            default -> {
                appendUserStats(sb);
                appendClassStats(sb);
            }
        }

        return sb.toString();
    }

    // ── private helpers ───────────────────────────────────────────────────

    private void appendUserStats(StringBuilder sb) {
        long totalUsers = usersRepo.count();
        sb.append("=== THỐNG KÊ NGƯỜI DÙNG ===\n");
        sb.append("Tổng số tài khoản: ").append(totalUsers).append("\n");
        sb.append("(Để xem chi tiết từng vai trò, vào module Quản lý người dùng.)\n\n");
    }

    private void appendClassStats(StringBuilder sb) {
        long totalClasses = lopHocPhanRepo.count();
        long totalSemesters = hocKiRepo.count();
        sb.append("=== THỐNG KÊ LỚP HỌC PHẦN ===\n");
        sb.append("Tổng số lớp học phần: ").append(totalClasses).append("\n");
        sb.append("Tổng số học kỳ: ").append(totalSemesters).append("\n\n");
    }

    private void appendSemesterStats(StringBuilder sb) {
        long totalSemesters = hocKiRepo.count();
        sb.append("=== HỌC KỲ ===\n");
        sb.append("Tổng số học kỳ đã tạo: ").append(totalSemesters).append("\n");
        sb.append("(Để xem danh sách chi tiết, vào module Quản lý học kỳ.)\n\n");
    }

    private void appendFinancialOverview(StringBuilder sb) {
        try {
            HocPhiAdminResponseDTO.DashboardTongQuan overview = hocPhiRepo.getDashboardTongQuan();
            sb.append("=== TỔNG QUAN TÀI CHÍNH (học phí) ===\n");
            sb.append("Tổng số hóa đơn: ").append(overview.getTongSoHocPhi()).append("\n");
            sb.append("Tổng số tiền: ").append(String.format("%,.0f VNĐ", overview.getTongSoTien())).append("\n");
            sb.append("Đã thanh toán: ").append(overview.getSoDaThanhToan()).append(" hóa đơn\n");
            sb.append("Chưa thanh toán: ").append(overview.getSoChuaThanhToan()).append(" hóa đơn\n");
            sb.append("Quá hạn: ").append(overview.getSoQuaHan()).append(" hóa đơn\n\n");
        } catch (Exception e) {
            sb.append("=== TỔNG QUAN TÀI CHÍNH ===\nKhông thể tải dữ liệu tài chính lúc này.\n\n");
        }
    }

    private void appendNotifications(StringBuilder sb, UUID userId) {
        List<ThongBaoResponse> tbList = thongBaoRepo.findThongBaoByUsersId(userId);
        sb.append("=== THÔNG BÁO MỚI NHẤT ===\n");
        if (tbList.isEmpty()) {
            sb.append("Không có thông báo nào.\n");
        } else {
            for (ThongBaoResponse tb : tbList.stream().limit(5).collect(Collectors.toList())) {
                sb.append("- [").append(Boolean.TRUE.equals(tb.getDaNhan()) ? "Đã đọc" : "Chưa đọc").append("] ")
                        .append(tb.getTieuDe()).append("\n");
            }
        }
        sb.append("\n");
    }

    // ── utils ─────────────────────────────────────────────────────────────

    private boolean hasRole(CustomUserDetails u, String role) {
        return u.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_" + role));
    }
}
