package com.university.service.student;

import com.university.config.SecurityUtils;
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
import com.university.service.student.payment.PaymentGatewayPort;
import com.university.service.student.payment.PaymentGatewayResult;
import com.university.service.student.payment.PaymentMethodProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class HocPhiStudentService {

    private static final long IDEMPOTENCY_TTL_MINUTES = 10;
    private static final String PAYMENT_SUCCESS = "THANH_CONG";
    private static final String PAYMENT_FAILED = "THAT_BAI";

    private final HocPhiStudentRepository hocPhiStudentRepository;
    private final ThanhToanHocPhiStudentRepository thanhToanHocPhiStudentRepository;
    private final PaymentGatewayPort paymentGatewayPort;
    private final PaymentMethodProvider paymentMethodProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public HocPhiTongQuanStudentResponse getTongQuanHocPhi() {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        List<HocPhi> hocPhiList = getHocPhiListByHocVienId(hocVienId);

        double tongCanThanhToan = hocPhiList.stream()
                .filter(hp -> hp.getTrangThai() != HocPhiEnum.DA_THANH_TOAN)
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

        return HocPhiTongQuanStudentResponse.builder()
                .tongCanThanhToan(tongCanThanhToan)
                .tongDaThanhToan(tongDaThanhToan)
                .tongQuaHan(tongQuaHan)
                .danhSachHocPhi(hocPhiList.stream()
                        .map(this::toHocPhiItemResponse)
                        .toList())
                .build();
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

    public ThanhToanHocPhiStudentResponse thanhToanOnline(ThanhToanHocPhiStudentRequest request) {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        String method = normalizeMethod(request.getPhuongThucThanhToan());
        String idempotencyKey = normalizeIdempotencyKey(request.getIdempotencyKey());
        acquireIdempotencyLock(hocVienId, request.getHocPhiId(), idempotencyKey);

        boolean keepIdempotencyLock = false;
        try {
            HocPhi hocPhi = getOwnedHocPhiForUpdate(request.getHocPhiId(), hocVienId);

            validatePaymentEligibility(hocPhi, method);

            PaymentGatewayResult gatewayResult = paymentGatewayPort.processPayment(hocPhi, method);
            if (!gatewayResult.success()) {
                return buildFailedPaymentResponse(hocPhi.getId(), method, gatewayResult.message());
            }

            ThanhToanHocPhi thanhToan = persistSuccessfulPayment(hocPhi, method, gatewayResult);
            keepIdempotencyLock = true;

            return buildSuccessfulPaymentResponse(hocPhi, thanhToan, method);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Yeu cau thanh toan nay da duoc xu ly truoc do");
        } finally {
            if (!keepIdempotencyLock) {
                releaseIdempotencyLock(hocVienId, request.getHocPhiId(), idempotencyKey);
            }
        }
    }

    @Transactional(readOnly = true)
    public LichSuThanhToanHocPhiStudentResponse getLichSuThanhToan() {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
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
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        HocPhi hocPhi = getOwnedHocPhi(hocPhiId, hocVienId);

        String content = buildInvoiceContent(hocPhi);
        return buildDownloadResponse(content, "hoa-don-" + hocPhi.getId() + ".txt");
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> taiBienLai(UUID hocPhiId) {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        HocPhi hocPhi = getOwnedHocPhi(hocPhiId, hocVienId);

        if (hocPhi.getThanhToanHocPhi() == null) {
            throw new IllegalStateException("Hoc phi nay chua duoc thanh toan");
        }

        String content = buildReceiptContent(hocPhi);
        return buildDownloadResponse(content, "bien-lai-" + hocPhi.getId() + ".txt");
    }

    private void validatePaymentEligibility(HocPhi hocPhi, String method) {
        if (!paymentMethodProvider.supports(method)) {
            throw new IllegalArgumentException("Phuong thuc thanh toan khong duoc ho tro");
        }
        if (hocPhi.getTrangThai() == HocPhiEnum.DA_THANH_TOAN || hocPhi.getThanhToanHocPhi() != null) {
            throw new IllegalStateException("Hoc phi nay da duoc thanh toan");
        }
        if (hocPhi.getSoTien() == null || hocPhi.getSoTien() <= 0) {
            throw new IllegalStateException("So tien thanh toan khong hop le");
        }
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

    private ThanhToanHocPhi persistSuccessfulPayment(HocPhi hocPhi, String method, PaymentGatewayResult gatewayResult) {
        ThanhToanHocPhi thanhToan = new ThanhToanHocPhi();
        thanhToan.setHocPhi(hocPhi);
        thanhToan.setFileChungTu(buildReceiptFileName(hocPhi));
        thanhToan.setPhuongThucThanhToan(method);
        thanhToan.setMaGiaoDichGateway(gatewayResult.gatewayReference());
        ThanhToanHocPhi saved = thanhToanHocPhiStudentRepository.save(thanhToan);

        hocPhi.setThanhToanHocPhi(saved);
        hocPhi.setTrangThai(HocPhiEnum.DA_THANH_TOAN);
        return saved;
    }

    private ThanhToanHocPhiStudentResponse buildFailedPaymentResponse(UUID hocPhiId, String method, String message) {
        return ThanhToanHocPhiStudentResponse.builder()
                .hocPhiId(hocPhiId)
                .trangThaiGiaoDich(PAYMENT_FAILED)
                .thongDiep(message)
                .phuongThucThanhToan(method)
                .build();
    }

    private ThanhToanHocPhiStudentResponse buildSuccessfulPaymentResponse(
            HocPhi hocPhi,
            ThanhToanHocPhi thanhToan,
            String method) {
        return ThanhToanHocPhiStudentResponse.builder()
                .hocPhiId(hocPhi.getId())
                .thanhToanId(thanhToan.getId())
                .trangThaiGiaoDich(PAYMENT_SUCCESS)
                .thongDiep("Thanh toan hoc phi thanh cong")
                .phuongThucThanhToan(method)
                .ngayThanhToan(thanhToan.getNgayThanhToan())
                .fileChungTu(thanhToan.getFileChungTu())
                .taiBienLaiUrl("/api/student/hoc-phi/" + hocPhi.getId() + "/receipt")
                .build();
    }

    private void acquireIdempotencyLock(UUID hocVienId, UUID hocPhiId, String idempotencyKey) {
        String key = buildIdempotencyKey(hocVienId, hocPhiId, idempotencyKey);
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "1", IDEMPOTENCY_TTL_MINUTES, TimeUnit.MINUTES);
        if (!Boolean.TRUE.equals(acquired)) {
            throw new IllegalStateException("Yeu cau thanh toan dang duoc xu ly hoac da duoc gui truoc do");
        }
    }

    private void releaseIdempotencyLock(UUID hocVienId, UUID hocPhiId, String idempotencyKey) {
        redisTemplate.delete(buildIdempotencyKey(hocVienId, hocPhiId, idempotencyKey));
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
                .daThanhToan(thanhToan != null)
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

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }

    private String buildInvoiceContent(HocPhi hocPhi) {
        return """
                HOA DON HOC PHI
                Hoc vien: %s
                Ma hoc vien: %s
                Hoc phi ID: %s
                Hoc ky: %s - %s
                So tin chi: %d
                So tien: %.2f
                Trang thai: %s
                Ngay tao: %s
                """
                .formatted(
                        hocPhi.getHocVien().getUsers().getHoTen(),
                        hocPhi.getHocVien().getMaHocVien(),
                        hocPhi.getId(),
                        hocPhi.getHocKi().getMaHocKi(),
                        hocPhi.getHocKi().getTenHocKi(),
                        hocPhi.getSoTinChi(),
                        hocPhi.getSoTien(),
                        hocPhi.getTrangThai(),
                        hocPhi.getCreatedAt());
    }

    private String buildReceiptContent(HocPhi hocPhi) {
        ThanhToanHocPhi thanhToan = hocPhi.getThanhToanHocPhi();
        return """
                BIEN LAI THANH TOAN HOC PHI
                Hoc vien: %s
                Ma hoc vien: %s
                Hoc phi ID: %s
                Thanh toan ID: %s
                Hoc ky: %s - %s
                So tien da thanh toan: %.2f
                Phuong thuc: %s
                Ma giao dich cong thanh toan: %s
                Ngay thanh toan: %s
                Chung tu: %s
                """
                .formatted(
                        hocPhi.getHocVien().getUsers().getHoTen(),
                        hocPhi.getHocVien().getMaHocVien(),
                        hocPhi.getId(),
                        thanhToan.getId(),
                        hocPhi.getHocKi().getMaHocKi(),
                        hocPhi.getHocKi().getTenHocKi(),
                        hocPhi.getSoTien(),
                        thanhToan.getPhuongThucThanhToan(),
                        thanhToan.getMaGiaoDichGateway(),
                        thanhToan.getNgayThanhToan(),
                        thanhToan.getFileChungTu());
    }

    private String normalizeMethod(String rawMethod) {
        return rawMethod == null ? "" : rawMethod.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeIdempotencyKey(String rawKey) {
        String normalizedKey = rawKey == null ? "" : rawKey.trim();
        if (normalizedKey.isEmpty()) {
            throw new IllegalArgumentException("Idempotency key khong duoc de trong");
        }
        return normalizedKey;
    }

    private String buildReceiptFileName(HocPhi hocPhi) {
        return "receipt-" + hocPhi.getHocKi().getMaHocKi() + "-" + hocPhi.getId() + ".txt";
    }

    private String buildIdempotencyKey(UUID hocVienId, UUID hocPhiId, String idempotencyKey) {
        return "student-payment:idempotency:" + hocVienId + ":" + hocPhiId + ":" + idempotencyKey;
    }
}
