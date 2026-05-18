package com.university.service.student;

import com.university.dto.request.student.ThanhToanHocPhiStudentRequest;
import com.university.dto.response.student.HocPhiStudentItemResponse;
import com.university.dto.response.student.HocPhiTongQuanStudentResponse;
import com.university.dto.response.student.LichSuThanhToanHocPhiStudentResponse;
import com.university.dto.response.student.PhuongThucThanhToanStudentResponse;
import com.university.dto.response.student.ThanhToanHocPhiStudentResponse;
import com.university.entity.HocPhi;
import com.university.entity.ThanhToanHocPhi;
import com.university.enums.HocPhiEnum;
import com.university.exception.ResourceNotFoundException;
import com.university.repository.student.HocPhiStudentRepository;
import com.university.repository.student.ThanhToanHocPhiStudentRepository;
import com.university.service.student.payment.PaymentMethodProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class HocPhiStudentService {

    private final HocPhiStudentRepository hocPhiStudentRepository;
    private final ThanhToanHocPhiStudentRepository thanhToanHocPhiStudentRepository;
    private final PaymentMethodProvider paymentMethodProvider;
    private final CurrentHocVienService currentHocVienService;

    public HocPhiTongQuanStudentResponse getTongQuanHocPhi() {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        List<HocPhi> hocPhiList = getHocPhiListByHocVienId(hocVienId);

        // Ghi QUA_HAN vào DB ngay khi học viên truy cập
        capNhatQuaHan(hocPhiList);

        double tongCanThanhToan = hocPhiList.stream()
                .filter(hp -> hp.getTrangThai() == HocPhiEnum.CHUA_THANH_TOAN
                        || hp.getTrangThai() == HocPhiEnum.QUA_HAN)
                .mapToDouble(HocPhi::getSoTien)
                .sum();

        double tongDaThanhToan = hocPhiList.stream()
                .filter(hp -> hp.getTrangThai() == HocPhiEnum.DA_THANH_TOAN)
                .mapToDouble(HocPhi::getSoTien)
                .sum();

        double tongQuaHan = hocPhiList.stream()
                .filter(hp -> hp.getTrangThai() == HocPhiEnum.QUA_HAN)
                .mapToDouble(HocPhi::getSoTien)
                .sum();

        // Thông tin sinh viên (dùng cho hóa đơn PDF)
        String maHocVien = "";
        String hoTen = "";
        String tenNganh = "";
        if (!hocPhiList.isEmpty()) {
            var hv = hocPhiList.get(0).getHocVien();
            maHocVien = hv.getMaHocVien() != null ? hv.getMaHocVien() : "";
            if (hv.getUsers() != null && hv.getUsers().getHoTen() != null) {
                hoTen = hv.getUsers().getHoTen();
            }
            if (hv.getNganh() != null && hv.getNganh().getTenNganh() != null) {
                tenNganh = hv.getNganh().getTenNganh();
            }
        }

        return HocPhiTongQuanStudentResponse.builder()
                .tongCanThanhToan(tongCanThanhToan)
                .tongDaThanhToan(tongDaThanhToan)
                .tongQuaHan(tongQuaHan)
                .maHocVien(maHocVien)
                .hoTen(hoTen)
                .tenNganh(tenNganh)
                .danhSachHocPhi(hocPhiList.stream()
                        .map(this::toHocPhiItemResponse)
                        .toList())
                .build();
    }

    private void capNhatQuaHan(List<HocPhi> hocPhiList) {
        LocalDateTime now = LocalDateTime.now();
        hocPhiList.stream()
                .filter(hp -> hp.getTrangThai() == HocPhiEnum.CHUA_THANH_TOAN)
                .filter(hp -> {
                    LocalDateTime ngayKetThuc = hp.getHocKi().getNgayKetThuc();
                    return ngayKetThuc != null && now.isAfter(ngayKetThuc.minusMonths(1));
                })
                .forEach(hp -> {
                    hp.setTrangThai(HocPhiEnum.QUA_HAN);
                    hocPhiStudentRepository.save(hp);
                });
    }

    @Transactional(readOnly = true)
    public List<PhuongThucThanhToanStudentResponse> getDanhSachPhuongThucThanhToan() {
        return paymentMethodProvider.getSupportedMethods().entrySet().stream()
                .map(entry -> PhuongThucThanhToanStudentResponse.builder()
                        .maPhuongThuc(entry.getKey())
                        .tenPhuongThuc(entry.getKey().replace('_', ' '))
                        .moTa(entry.getValue())
                        .build())
                .toList();
    }

    public ThanhToanHocPhiStudentResponse nopChungTu(ThanhToanHocPhiStudentRequest request) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        String method = request.getPhuongThucThanhToan().trim().toUpperCase(Locale.ROOT);

        HocPhi hocPhi = getOwnedHocPhiForUpdate(request.getHocPhiId(), hocVienId);

        if (hocPhi.getTrangThai() == HocPhiEnum.DA_THANH_TOAN) {
            throw new IllegalStateException("Hoc phi nay da duoc thanh toan");
        }
        if (hocPhi.getTrangThai() == HocPhiEnum.DANG_XU_LY) {
            throw new IllegalStateException("Chung tu da duoc nop, vui long cho ke toan xac nhan");
        }

        ThanhToanHocPhi thanhToan = new ThanhToanHocPhi();
        thanhToan.setHocPhi(hocPhi);
        thanhToan.setPhuongThucThanhToan(method);
        thanhToan.setFileChungTu(request.getFileChungTu());
        thanhToan.setNgayThanhToan(
                request.getNgayThanhToan() != null
                        ? request.getNgayThanhToan().atStartOfDay()
                        : LocalDateTime.now());
        thanhToan.setMaGiaoDichGateway(UUID.randomUUID().toString());
        thanhToanHocPhiStudentRepository.save(thanhToan);

        hocPhi.setThanhToanHocPhi(thanhToan);
        hocPhi.setTrangThai(HocPhiEnum.DANG_XU_LY);
        hocPhiStudentRepository.save(hocPhi);

        return ThanhToanHocPhiStudentResponse.builder()
                .hocPhiId(hocPhi.getId())
                .thanhToanId(thanhToan.getId())
                .trangThaiGiaoDich("DANG_XU_LY")
                .thongDiep("Chung tu da duoc nop, cho ke toan xac nhan")
                .phuongThucThanhToan(method)
                .fileChungTu(request.getFileChungTu())
                .build();
    }

    @Transactional(readOnly = true)
    public LichSuThanhToanHocPhiStudentResponse getLichSuThanhToan() {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        List<HocPhi> hocPhiList = getHocPhiListByHocVienId(hocVienId)
                .stream()
                .filter(hp -> hp.getThanhToanHocPhi() != null)
                .toList();

        return LichSuThanhToanHocPhiStudentResponse.builder()
                .lichSuThanhToan(hocPhiList.stream()
                        .map(this::toHocPhiItemResponse)
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> taiHoaDon(UUID hocPhiId) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        HocPhi hocPhi = getOwnedHocPhi(hocPhiId, hocVienId);
        return buildDownloadResponse(buildInvoiceContent(hocPhi), "hoa-don-" + hocPhi.getId() + ".txt");
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> taiBienLai(UUID hocPhiId) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        HocPhi hocPhi = getOwnedHocPhi(hocPhiId, hocVienId);
        if (hocPhi.getThanhToanHocPhi() == null) {
            throw new IllegalStateException("Hoc phi nay chua duoc thanh toan");
        }
        return buildDownloadResponse(buildReceiptContent(hocPhi), "bien-lai-" + hocPhi.getId() + ".txt");
    }

    private List<HocPhi> getHocPhiListByHocVienId(UUID hocVienId) {
        return hocPhiStudentRepository.findAllByHocVienIdWithDetails(hocVienId);
    }

    private HocPhi getOwnedHocPhi(UUID hocPhiId, UUID hocVienId) {
        return hocPhiStudentRepository.findOwnedByHocVienId(hocPhiId, hocVienId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hoc phi"));
    }

    private HocPhi getOwnedHocPhiForUpdate(UUID hocPhiId, UUID hocVienId) {
        return hocPhiStudentRepository.findOwnedByHocVienIdForUpdate(hocPhiId, hocVienId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hoc phi"));
    }

    private HocPhiStudentItemResponse toHocPhiItemResponse(HocPhi hocPhi) {
        ThanhToanHocPhi thanhToan = hocPhi.getThanhToanHocPhi();
        return HocPhiStudentItemResponse.builder()
                .hocPhiId(hocPhi.getId())
                .hocKiId(hocPhi.getHocKi().getId())
                .maHocKi(hocPhi.getHocKi().getMaHocKi())
                .tenHocKi(hocPhi.getHocKi().getTenHocKi())
                .soTien(hocPhi.getSoTien())
                .soTinChi(hocPhi.getSoTinChi())
                .trangThai(hocPhi.getTrangThai())
                .ngayThanhToan(thanhToan != null ? thanhToan.getNgayThanhToan() : null)
                .fileChungTu(thanhToan != null ? thanhToan.getFileChungTu() : null)
                .phuongThucThanhToan(thanhToan != null ? thanhToan.getPhuongThucThanhToan() : null)
                .daThanhToan(hocPhi.getTrangThai() == HocPhiEnum.DA_THANH_TOAN)
                .taiLieuTaiVe(thanhToan != null
                        ? "/api/student/hoc-phi/" + hocPhi.getId() + "/receipt"
                        : "/api/student/hoc-phi/" + hocPhi.getId() + "/invoice")
                .build();
    }

    private ResponseEntity<byte[]> buildDownloadResponse(String content, String fileName) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    private String buildInvoiceContent(HocPhi hocPhi) {
        return """
                HOA DON HOC PHI
                Hoc vien: %s
                Hoc phi ID: %s
                Hoc ky: %s - %s
                So tin chi: %d
                So tien: %.0f VND
                Trang thai: %s
                Ngay tao: %s
                """.formatted(
                hocPhi.getHocVien().getUsers().getHoTen(),
                hocPhi.getId(),
                hocPhi.getHocKi().getMaHocKi(), hocPhi.getHocKi().getTenHocKi(),
                hocPhi.getSoTinChi(), hocPhi.getSoTien(),
                hocPhi.getTrangThai(), hocPhi.getCreatedAt());
    }

    private String buildReceiptContent(HocPhi hocPhi) {
        ThanhToanHocPhi tt = hocPhi.getThanhToanHocPhi();
        return """
                BIEN LAI THANH TOAN HOC PHI
                Hoc vien: %s
                Hoc phi ID: %s
                Hoc ky: %s - %s
                So tien: %.0f VND
                Phuong thuc: %s
                Ma giao dich: %s
                Ngay thanh toan: %s
                Chung tu: %s
                """.formatted(
                hocPhi.getHocVien().getUsers().getHoTen(),
                hocPhi.getId(),
                hocPhi.getHocKi().getMaHocKi(), hocPhi.getHocKi().getTenHocKi(),
                hocPhi.getSoTien(),
                tt.getPhuongThucThanhToan(), tt.getMaGiaoDichGateway(),
                tt.getNgayThanhToan(), tt.getFileChungTu());
    }
}
