package com.university.service.chatbot;

import com.university.dto.response.admin.HocPhiAdminResponseDTO;
import com.university.dto.response.student.ThongBaoResponse;
import com.university.repository.admin.HocPhiAdminRepository;
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
 * Provides chat context for accountants (ROLE_accountant).
 * Only exposes aggregate financial data — never raw personal payment records.
 */
@Component
@Order(3)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountantChatContextProvider implements ChatContextProvider {

    private final HocPhiAdminRepository hocPhiRepo;
    private final ThongBaoNguoiDungRepository thongBaoRepo;

    private static final String SYSTEM_PROMPT = """
            Bạn là trợ lý tài chính/kế toán thông minh của LearningHub dành cho nhân viên kế toán.
            Vai trò: hỗ trợ kế toán tra cứu công nợ học phí, hóa đơn, thanh toán và báo cáo tài chính.

            Quy tắc bắt buộc:
            - Chỉ trả lời dựa trên dữ liệu được cung cấp trong phần [Dữ liệu hệ thống].
            - Không bịa hoặc suy đoán số liệu tài chính — dữ liệu tài chính sai có thể gây hậu quả nghiêm trọng.
            - Khi trả lời số liệu tổng hợp, ghi rõ đây là số liệu thống kê, không phải hóa đơn cụ thể.
            - Không tiết lộ thông tin cá nhân (mật khẩu, CCCD) của sinh viên trong câu trả lời.
            - Nếu cần chi tiết cụ thể, hướng dẫn vào module Quản lý học phí.
            - Trả lời ngắn gọn, chính xác, bằng tiếng Việt.
            """;

    @Override
    public boolean supports(CustomUserDetails userDetails) {
        return hasRole(userDetails, "ACCOUNTANT");
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
            case DEBT_OVERVIEW -> {
                appendTongQuan(sb);
                appendTopNo(sb);
            }
            case INVOICE -> {
                appendTongQuan(sb);
                appendTheoHocKi(sb);
            }
            case PAYMENT -> {
                appendTongQuan(sb);
                appendTheoThang(sb);
            }
            case FINANCIAL_REPORT -> {
                appendTongQuan(sb);
                appendTheoHocKi(sb);
                appendTheoThang(sb);
            }
            case NOTIFICATION -> appendNotifications(sb, userId);
            case GENERAL -> {
                appendTongQuan(sb);
                appendTopNo(sb);
            }
            default -> appendTongQuan(sb);
        }

        return sb.toString();
    }

    // ── private helpers ───────────────────────────────────────────────────

    private void appendTongQuan(StringBuilder sb) {
        try {
            HocPhiAdminResponseDTO.DashboardTongQuan overview = hocPhiRepo.getDashboardTongQuan();
            sb.append("=== TỔNG QUAN HỌC PHÍ ===\n");
            sb.append("Tổng số hóa đơn: ").append(overview.getTongSoHocPhi()).append("\n");
            sb.append("Tổng số tiền phải thu: ").append(String.format("%,.0f VNĐ", overview.getTongSoTien())).append("\n");
            sb.append("Đã thanh toán: ").append(overview.getSoDaThanhToan()).append(" hóa đơn\n");
            sb.append("Chưa thanh toán: ").append(overview.getSoChuaThanhToan()).append(" hóa đơn\n");
            sb.append("Quá hạn: ").append(overview.getSoQuaHan()).append(" hóa đơn\n\n");
        } catch (Exception e) {
            sb.append("=== TỔNG QUAN HỌC PHÍ ===\nKhông thể tải dữ liệu lúc này.\n\n");
        }
    }

    private void appendTheoHocKi(StringBuilder sb) {
        try {
            List<HocPhiAdminResponseDTO.DashboardTheoHocKi> data = hocPhiRepo.getDashboardTheoHocKi();
            sb.append("=== HỌC PHÍ THEO HỌC KỲ ===\n");
            if (data.isEmpty()) {
                sb.append("Chưa có dữ liệu.\n");
            } else {
                for (HocPhiAdminResponseDTO.DashboardTheoHocKi hk : data.stream().limit(5).collect(Collectors.toList())) {
                    sb.append("- ").append(hk.getHocKiTen())
                            .append(": tổng ").append(String.format("%,.0f VNĐ", hk.getTongTien()))
                            .append(" | đã thu ").append(String.format("%,.0f VNĐ", hk.getTienDaThu()))
                            .append(" | còn nợ ").append(String.format("%,.0f VNĐ", hk.getTienConNo())).append("\n");
                }
            }
            sb.append("\n");
        } catch (Exception e) {
            sb.append("=== HỌC PHÍ THEO HỌC KỲ ===\nKhông thể tải dữ liệu.\n\n");
        }
    }

    private void appendTheoThang(StringBuilder sb) {
        try {
            List<HocPhiAdminResponseDTO.DashboardTheoThang> data = hocPhiRepo.getDashboardTheoThang();
            sb.append("=== THANH TOÁN GẦN ĐÂY (theo tháng) ===\n");
            if (data.isEmpty()) {
                sb.append("Chưa có dữ liệu.\n");
            } else {
                for (HocPhiAdminResponseDTO.DashboardTheoThang t : data.stream().limit(6).collect(Collectors.toList())) {
                    sb.append("- Tháng ").append(t.getThang()).append("/").append(t.getNam())
                            .append(": ").append(t.getSoLuong()).append(" giao dịch")
                            .append(" | đã thu ").append(String.format("%,.0f VNĐ", t.getTienDaThu())).append("\n");
                }
            }
            sb.append("\n");
        } catch (Exception e) {
            sb.append("=== THANH TOÁN GẦN ĐÂY ===\nKhông thể tải dữ liệu.\n\n");
        }
    }

    private void appendTopNo(StringBuilder sb) {
        try {
            List<HocPhiAdminResponseDTO.DashboardTopNo> topNo = hocPhiRepo.getDashboardTopNo();
            sb.append("=== SINH VIÊN NỢ HỌC PHÍ NHIỀU NHẤT ===\n");
            if (topNo.isEmpty()) {
                sb.append("Không có sinh viên nào nợ học phí.\n");
            } else {
                for (HocPhiAdminResponseDTO.DashboardTopNo no : topNo.stream().limit(5).collect(Collectors.toList())) {
                    sb.append("- ").append(no.getHoTen())
                            .append(" (MSSV: ").append(no.getMaHocVien()).append(")")
                            .append(" — nợ ").append(String.format("%,.0f VNĐ", no.getSoTienNo()))
                            .append(" (").append(no.getSoLanNo()).append(" khoản)\n");
                }
            }
            sb.append("\n");
        } catch (Exception e) {
            sb.append("=== CÔNG NỢ ===\nKhông thể tải dữ liệu.\n\n");
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
