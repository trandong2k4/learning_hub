package com.university.service.student;

import com.university.config.SecurityUtils;
import com.university.dto.response.student.DashboardStudentNotificationItemResponse;
import com.university.dto.response.student.DashboardStudentNotificationSummaryResponse;
import com.university.dto.response.student.DashboardStudentProfileSummaryResponse;
import com.university.dto.response.student.DashboardStudentResponse;
import com.university.dto.response.student.DashboardStudentTuitionSummaryResponse;
import com.university.dto.response.student.HocPhiTongQuanStudentResponse;
import com.university.dto.response.student.HocVienProfileResponseDTO;
import com.university.dto.response.student.LichCaNhanStudentResponse;
import com.university.dto.response.student.ThongBaoResponse;
import com.university.dto.response.student.TienDoHocTapTongQuanStudentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PreAuthorize("hasRole('STUDENT')")
public class DashboardStudentService {

    private final HocVienProfileService hocVienProfileService;
    private final LichStudentService lichStudentService;
    private final TienDoHocTapStudentService tienDoHocTapStudentService;
    private final ThongBaoService thongBaoService;
    private final HocPhiStudentService hocPhiStudentService;

    public DashboardStudentResponse getDashboard() {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        log.debug("Bat dau tai dashboard cho hocVienId={}", hocVienId);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        log.debug("Da tim thay hoc vien, bat dau goi song song du lieu dashboard");

        CompletableFuture<HocVienProfileResponseDTO> profileFuture = CompletableFuture.supplyAsync(
                withSecurityContext(hocVienProfileService::getProfile, securityContext));
        CompletableFuture<LichCaNhanStudentResponse> lichHomNayFuture = CompletableFuture.supplyAsync(
                withSecurityContext(() -> lichStudentService.getLichCaNhan("DAY", LocalDate.now()), securityContext));
        CompletableFuture<TienDoHocTapTongQuanStudentResponse> tienDoHocTapFuture = CompletableFuture.supplyAsync(
                withSecurityContext(tienDoHocTapStudentService::getTongQuanTienDoHocTap, securityContext));
        CompletableFuture<List<ThongBaoResponse>> thongBaoFuture = CompletableFuture.supplyAsync(
                withSecurityContext(thongBaoService::getDanhSachThongBao, securityContext));
        CompletableFuture<HocPhiTongQuanStudentResponse> hocPhiFuture = CompletableFuture.supplyAsync(
                withSecurityContext(hocPhiStudentService::getTongQuanHocPhi, securityContext));

        CompletableFuture.allOf(
                profileFuture,
                lichHomNayFuture,
                tienDoHocTapFuture,
                thongBaoFuture,
                hocPhiFuture
        ).join();

        log.debug("Da tai xong du lieu dashboard cho hocVienId={}", hocVienId);

        return DashboardStudentResponse.builder()
                .thongTinCaNhan(toProfileSummary(profileFuture.join()))
                .lichHomNay(lichHomNayFuture.join())
                .tienDoHocTap(tienDoHocTapFuture.join())
                .thongBao(toNotificationSummary(thongBaoFuture.join()))
                .hocPhi(toTuitionSummary(hocPhiFuture.join()))
                .build();
    }

    private DashboardStudentProfileSummaryResponse toProfileSummary(HocVienProfileResponseDTO profile) {
        return DashboardStudentProfileSummaryResponse.builder()
                .userId(profile.getId())
                .hoTen(profile.getHoTen())
                .maHocVien(profile.getMaHocVien())
                .email(profile.getEmail())
                .soDienThoai(profile.getSoDienThoai())
                .build();
    }

    private DashboardStudentNotificationSummaryResponse toNotificationSummary(List<ThongBaoResponse> thongBaoList) {
        List<DashboardStudentNotificationItemResponse> items = new ArrayList<>(thongBaoList.size());
        int soThongBaoChuaDoc = 0;

        for (ThongBaoResponse thongBao : thongBaoList) {
            items.add(DashboardStudentNotificationItemResponse.builder()
                    .id(thongBao.getId())
                    .tieuDe(thongBao.getTieuDe())
                    .createdAt(thongBao.getCreatedAt())
                    .daNhan(thongBao.getDaNhan())
                    .build());

            if (!Boolean.TRUE.equals(thongBao.getDaNhan())) {
                soThongBaoChuaDoc++;
            }
        }

        return DashboardStudentNotificationSummaryResponse.builder()
                .tongThongBao(thongBaoList.size())
                .soThongBaoChuaDoc(soThongBaoChuaDoc)
                .danhSachThongBao(items)
                .build();
    }

    private DashboardStudentTuitionSummaryResponse toTuitionSummary(HocPhiTongQuanStudentResponse hocPhi) {
        return DashboardStudentTuitionSummaryResponse.builder()
                .tongCanThanhToan(hocPhi.getTongCanThanhToan())
                .tongDaThanhToan(hocPhi.getTongDaThanhToan())
                .tongQuaHan(hocPhi.getTongQuaHan())
                .build();
    }

    private <T> Supplier<T> withSecurityContext(Supplier<T> supplier, SecurityContext parentContext) {
        return () -> {
            try {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(parentContext.getAuthentication());
                SecurityContextHolder.setContext(context);
                return supplier.get();
            } finally {
                SecurityContextHolder.clearContext();
            }
        };
    }
}
