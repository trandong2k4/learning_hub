package com.university.service.accounting;

import com.university.dto.response.accounting.BaoCaoThongKeOverviewResponse;
import com.university.dto.response.accounting.PaymentInfoResponse;
import com.university.entity.HocPhi;
import com.university.entity.ThanhToanHocPhi;
import com.university.enums.HocPhiEnum;
import com.university.repository.admin.HocPhiAdminRepository;
import com.university.repository.admin.ThanhToanHocPhiAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaoCaoThongKeService {

    private final HocPhiAdminRepository hocPhiAdminRepository;
    private final ThanhToanHocPhiAdminRepository thanhToanHocPhiAdminRepository;

    @Transactional(readOnly = true)
    public BaoCaoThongKeOverviewResponse getOverview(LocalDate start, LocalDate end) {
        LocalDateTime startDt = start == null ? null : start.atStartOfDay();
        LocalDateTime endDt = end == null ? null : end.atTime(23, 59, 59);

        // Lấy tất cả ThanhToanHocPhi (kèm HocPhi), filter bằng Java
        List<ThanhToanHocPhi> confirmedPayments = thanhToanHocPhiAdminRepository
                .findAllWithHocPhi()
                .stream()
                .filter(p -> p.getHocPhi() != null
                        && p.getHocPhi().getTrangThai() == HocPhiEnum.DA_THANH_TOAN)
                .filter(p -> {
                    LocalDateTime ref = p.getCreatedAt() != null ? p.getCreatedAt() : p.getNgayThanhToan();
                    if (ref == null) return true;
                    if (startDt != null && ref.isBefore(startDt)) return false;
                    if (endDt != null && ref.isAfter(endDt)) return false;
                    return true;
                })
                .collect(Collectors.toList());

        double tongDoanhThu = confirmedPayments.stream()
                .mapToDouble(p -> Optional.ofNullable(p.getHocPhi()).map(HocPhi::getSoTien).orElse(0.0))
                .sum();

        long soLuongThanhToan = confirmedPayments.size();

        Map<YearMonth, Double> monthly = confirmedPayments.stream()
                .collect(Collectors.groupingBy(p -> {
                    LocalDateTime ref = p.getNgayThanhToan() != null ? p.getNgayThanhToan() : p.getCreatedAt();
                    return YearMonth.from(ref != null ? ref : LocalDateTime.now());
                },
                        Collectors.summingDouble(
                                p -> Optional.ofNullable(p.getHocPhi()).map(HocPhi::getSoTien).orElse(0.0))));

        List<BaoCaoThongKeOverviewResponse.MonthlyRevenue> doanhThuTheoThang = monthly.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new BaoCaoThongKeOverviewResponse.MonthlyRevenue(e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());

        // Tổng hợp số dư toàn bộ (không lọc theo ngày, phản ánh trạng thái hiện tại)
        List<HocPhi> allHocPhis = hocPhiAdminRepository.findAll();

        double tongCanThanhToan = allHocPhis.stream()
                .filter(hp -> hp.getTrangThai() == HocPhiEnum.CHUA_THANH_TOAN
                           || hp.getTrangThai() == HocPhiEnum.QUA_HAN
                           || hp.getTrangThai() == HocPhiEnum.DANG_XU_LY)
                .mapToDouble(hp -> Optional.ofNullable(hp.getSoTien()).orElse(0.0))
                .sum();

        double tongDaThanhToan = allHocPhis.stream()
                .filter(hp -> hp.getTrangThai() == HocPhiEnum.DA_THANH_TOAN)
                .mapToDouble(hp -> Optional.ofNullable(hp.getSoTien()).orElse(0.0))
                .sum();

        double tongQuaHan = allHocPhis.stream()
                .filter(hp -> hp.getTrangThai() == HocPhiEnum.QUA_HAN)
                .mapToDouble(hp -> Optional.ofNullable(hp.getSoTien()).orElse(0.0))
                .sum();

        List<PaymentInfoResponse> recent = confirmedPayments.stream()
                .sorted(Comparator.comparing(
                        (ThanhToanHocPhi p) -> p.getNgayThanhToan() != null ? p.getNgayThanhToan() : p.getCreatedAt(),
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(10)
                .map(this::toPaymentInfo)
                .collect(Collectors.toList());

        return BaoCaoThongKeOverviewResponse.builder()
                .tongDoanhThu(tongDoanhThu)
                .soLuongThanhToan(soLuongThanhToan)
                .tongCanThanhToan(tongCanThanhToan)
                .tongDaThanhToan(tongDaThanhToan)
                .tongQuaHan(tongQuaHan)
                .doanhThuTheoThang(doanhThuTheoThang)
                .danhSachThanhToanMoiNhat(recent)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PaymentInfoResponse> getPayments(LocalDate start, LocalDate end) {
        LocalDateTime startDt = start == null ? null : start.atStartOfDay();
        LocalDateTime endDt = end == null ? null : end.atTime(23, 59, 59);

        return thanhToanHocPhiAdminRepository.findAllWithHocPhi().stream()
                .filter(p -> p.getHocPhi() != null
                        && p.getHocPhi().getTrangThai() == HocPhiEnum.DA_THANH_TOAN)
                .filter(p -> {
                    LocalDateTime ref = p.getNgayThanhToan() != null ? p.getNgayThanhToan() : p.getCreatedAt();
                    if (ref == null) return true;
                    if (startDt != null && ref.isBefore(startDt)) return false;
                    if (endDt != null && ref.isAfter(endDt)) return false;
                    return true;
                })
                .map(this::toPaymentInfo)
                .collect(Collectors.toList());
    }

    private PaymentInfoResponse toPaymentInfo(ThanhToanHocPhi p) {
        PaymentInfoResponse dto = new PaymentInfoResponse();
        dto.setPaymentId(p.getId());
        if (p.getHocPhi() != null) {
            dto.setHocPhiId(p.getHocPhi().getId());
            if (p.getHocPhi().getHocVien() != null && p.getHocPhi().getHocVien().getUsers() != null) {
                dto.setHocVienId(p.getHocPhi().getHocVien().getId());
                dto.setHocVienName(p.getHocPhi().getHocVien().getUsers().getHoTen());
            }
            dto.setAmount(Optional.ofNullable(p.getHocPhi().getSoTien()).orElse(0.0));
        }
        dto.setNgayThanhToan(p.getNgayThanhToan());
        dto.setPhuongThucThanhToan(p.getPhuongThucThanhToan());
        dto.setMaGiaoDichGateway(p.getMaGiaoDichGateway());
        dto.setFileChungTu(p.getFileChungTu());
        return dto;
    }

}
