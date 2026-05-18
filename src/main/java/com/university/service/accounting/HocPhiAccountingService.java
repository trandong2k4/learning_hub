package com.university.service.accounting;

import com.university.config.SecurityUtils;
import com.university.dto.request.Notification.NotificationRequest;
import com.university.dto.request.admin.ThanhToanHocPhiAdminRequestDTO;
import com.university.dto.response.admin.ThanhToanHocPhiAdminResponseDTO;
import com.university.dto.response.accounting.AccountingHocPhiResponse;
import com.university.dto.response.accounting.AccountingInvoiceCandidateResponse;
import com.university.dto.response.accounting.AccountingInvoiceGenerationResponse;
import com.university.dto.response.accounting.AccountingInvoiceSemesterResponse;
import com.university.dto.response.accounting.AccountingStudentLedgerResponse;
import com.university.entity.HocPhi;
import com.university.entity.HocKi;
import com.university.entity.HocVien;
import com.university.entity.ThanhToanHocPhi;
import com.university.enums.LoaiThongBaoEnum;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.ThanhToanHocPhiAdminMapper;
import com.university.repository.admin.HocVienAdminRepository;
import com.university.repository.admin.HocPhiAdminRepository;
import com.university.repository.admin.HocKiAdminRepository;
import com.university.repository.admin.DangKyTinChiAdminRepository;
import com.university.repository.admin.ThanhToanHocPhiAdminRepository;
import com.university.service.Notification.NotificationService;
import com.university.service.mail.SendGridMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HocPhiAccountingService {

    private static final String NOTIFY_PREFIX = "tuition_notify:";
    private static final Duration NOTIFY_TTL = Duration.ofDays(7);
    private static final Duration RATE_LIMIT_TTL = Duration.ofMinutes(2);
    private static final double DON_GIA_TIN_CHI = 700_000.0;

    private final HocPhiAdminRepository hocPhiAdminRepository;
    private final ThanhToanHocPhiAdminRepository thanhToanHocPhiAdminRepository;
    private final ThanhToanHocPhiAdminMapper thanhToanMapper;
    private final SendGridMailService sendGridMailService;
    private final StringRedisTemplate redisTemplate;
    private final HocVienAdminRepository hocVienAdminRepository;
    private final HocKiAdminRepository hocKiAdminRepository;
    private final DangKyTinChiAdminRepository dangKyTinChiAdminRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<AccountingHocPhiResponse> getDueHocPhi() {
        return hocPhiAdminRepository.findAll().stream()
                .filter(hp -> hp.getTrangThai() == com.university.enums.HocPhiEnum.CHUA_THANH_TOAN
                        || hp.getTrangThai() == com.university.enums.HocPhiEnum.QUA_HAN
                        || hp.getTrangThai() == com.university.enums.HocPhiEnum.DANG_XU_LY)
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountingHocPhiResponse> getAllHocPhi() {
        return hocPhiAdminRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountingStudentLedgerResponse getStudentLedger(UUID hocVienId) {
        List<HocPhi> hocPhiList = hocPhiAdminRepository.findAllByHocVienId(hocVienId);
        if (hocPhiList.isEmpty()) {
            throw new SimpleMessageException("Không tìm thấy dữ liệu học phí của học viên");
        }

        HocVien hocVien = hocPhiList.get(0).getHocVien();
        List<AccountingHocPhiResponse> items = hocPhiList.stream()
                .map(this::toDto)
                .toList();

        List<AccountingHocPhiResponse> congNoItems = items.stream()
                .filter(item -> item.getTrangThai() == com.university.enums.HocPhiEnum.CHUA_THANH_TOAN
                        || item.getTrangThai() == com.university.enums.HocPhiEnum.DANG_XU_LY
                        || item.getTrangThai() == com.university.enums.HocPhiEnum.QUA_HAN)
                .sorted(Comparator.comparing(AccountingHocPhiResponse::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        List<AccountingHocPhiResponse> lichSuThanhToan = items.stream()
                .filter(item -> item.getNgayThanhToan() != null
                        || item.getPhuongThucThanhToan() != null
                        || item.getMaGiaoDich() != null)
                .sorted(Comparator.comparing(
                        item -> Optional.ofNullable(item.getNgayThanhToan()).orElse(item.getCreatedAt()),
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        double tongCongNo = congNoItems.stream()
                .mapToDouble(item -> Optional.ofNullable(item.getSoTien()).orElse(0.0))
                .sum();

        double tongDaThanhToan = items.stream()
                .filter(item -> item.getTrangThai() == com.university.enums.HocPhiEnum.DA_THANH_TOAN)
                .mapToDouble(item -> Optional.ofNullable(item.getSoTien()).orElse(0.0))
                .sum();

        double tongQuaHan = items.stream()
                .filter(item -> item.getTrangThai() == com.university.enums.HocPhiEnum.QUA_HAN)
                .mapToDouble(item -> Optional.ofNullable(item.getSoTien()).orElse(0.0))
                .sum();

        return AccountingStudentLedgerResponse.builder()
                .hocVienId(hocVien.getId())
                .maHocVien(hocVien.getMaHocVien())
                .hocVienName(hocVien.getUsers() != null ? hocVien.getUsers().getHoTen() : null)
                .hocVienEmail(hocVien.getUsers() != null ? hocVien.getUsers().getEmail() : null)
                .tongCongNo(tongCongNo)
                .tongDaThanhToan(tongDaThanhToan)
                .tongQuaHan(tongQuaHan)
                .soKhoanCongNo(congNoItems.size())
                .congNoItems(congNoItems)
                .lichSuThanhToan(lichSuThanhToan)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AccountingInvoiceSemesterResponse> getInvoiceSemesters() {
        return dangKyTinChiAdminRepository.findDangKyTinChiTongHopTheoHocKi().stream()
                .map(item -> AccountingInvoiceSemesterResponse.builder()
                        .hocKiId(item.getHocKiId())
                        .hocKiMa(item.getHocKiMa())
                        .hocKiName(item.getHocKiTen())
                        .soHocVienDangKy(item.getSoHocVien())
                        .tongTinChi(item.getTongTinChi())
                        .tongTienDuKien(item.getTongTien())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountingInvoiceGenerationResponse previewInvoices(UUID hocKiId) {
        HocKi hocKi = hocKiAdminRepository.findById(hocKiId)
                .orElseThrow(() -> new SimpleMessageException("Học kỳ không tồn tại"));
        List<AccountingInvoiceCandidateResponse> items = buildInvoiceCandidates(hocKiId);
        int soHoaDonBoQua = (int) items.stream()
                .filter(item -> Boolean.TRUE.equals(item.getDaCoHoaDon()))
                .count();
        return buildInvoiceResponse(hocKi, items, 0, soHoaDonBoQua, 0.0);
    }

    @Transactional
    public AccountingInvoiceGenerationResponse generateInvoices(UUID hocKiId) {
        HocKi hocKi = hocKiAdminRepository.findById(hocKiId)
                .orElseThrow(() -> new SimpleMessageException("Học kỳ không tồn tại"));
        List<AccountingInvoiceCandidateResponse> candidates = buildInvoiceCandidates(hocKiId);
        int soHoaDonBoQua = (int) candidates.stream()
                .filter(item -> Boolean.TRUE.equals(item.getDaCoHoaDon()))
                .count();

        List<HocPhi> toCreate = new ArrayList<>();
        for (AccountingInvoiceCandidateResponse candidate : candidates) {
            if (Boolean.TRUE.equals(candidate.getDaCoHoaDon())) {
                continue;
            }
            HocVien hocVien = hocVienAdminRepository.findById(candidate.getHocVienId())
                    .orElseThrow(() -> new SimpleMessageException("Không tìm thấy học viên"));
            HocPhi hocPhi = new HocPhi();
            hocPhi.setHocVien(hocVien);
            hocPhi.setHocKi(hocKi);
            hocPhi.setSoTinChi(candidate.getTongSoTinChi());
            hocPhi.setSoTien(candidate.getSoTien());
            hocPhi.setTrangThai(com.university.enums.HocPhiEnum.CHUA_THANH_TOAN);
            toCreate.add(hocPhi);
        }

        List<HocPhi> created = hocPhiAdminRepository.saveAll(toCreate);
        double tongTienDaTao = created.stream()
                .mapToDouble(item -> Optional.ofNullable(item.getSoTien()).orElse(0.0))
                .sum();

        List<AccountingInvoiceCandidateResponse> refreshedItems = buildInvoiceCandidates(hocKiId);
        return buildInvoiceResponse(hocKi, refreshedItems, created.size(), soHoaDonBoQua, tongTienDaTao);
    }

    @Transactional
    public void xacNhanThanhToan(UUID hocPhiId) {
        String confirmRateKey = "rate_limit:tuition_confirm:" + hocPhiId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(confirmRateKey))) {
            throw new SimpleMessageException("Yêu cầu đang được xử lý, vui lòng chờ");
        }

        HocPhi hocPhi = hocPhiAdminRepository.findById(hocPhiId)
                .orElseThrow(() -> new SimpleMessageException("Học phí không tồn tại"));
        if (hocPhi.getTrangThai() == com.university.enums.HocPhiEnum.DA_THANH_TOAN) {
            return;
        }
        if (hocPhi.getTrangThai() != com.university.enums.HocPhiEnum.DANG_XU_LY) {
            throw new SimpleMessageException("Học phí chưa được học viên nộp chứng từ");
        }

        redisTemplate.opsForValue().set(confirmRateKey, "1", Duration.ofSeconds(30));

        hocPhi.setTrangThai(com.university.enums.HocPhiEnum.DA_THANH_TOAN);
        hocPhiAdminRepository.save(hocPhi);

        if (hocPhi.getHocVien() != null && hocPhi.getHocVien().getUsers() != null) {
            try {
                UUID studentUsersId = hocPhi.getHocVien().getUsers().getId();
                UUID senderId = SecurityUtils.getCurrentUserId();
                String tenHocKi = hocPhi.getHocKi() != null ? hocPhi.getHocKi().getTenHocKi() : "";
                String soTienStr = String.format("%,.0f", hocPhi.getSoTien());

                NotificationRequest notifRequest = new NotificationRequest();
                notifRequest.setTieuDe("Học phí " + tenHocKi + " đã được xác nhận");
                notifRequest.setNoiDung("Học phí kỳ " + tenHocKi + " số tiền " + soTienStr
                        + " VND của bạn đã được kế toán xác nhận thành công.");
                notifRequest.setLoaiThongBao(LoaiThongBaoEnum.THONG_BAO_HOC_PHI);
                notifRequest.setUserIds(List.of(studentUsersId));
                notificationService.sendNotification(notifRequest, senderId);
            } catch (Exception ex) {
                log.warn("Gửi thông báo xác nhận học phí thất bại hocPhiId={}: {}", hocPhiId, ex.getMessage());
            }
        }
    }

    @Transactional
    public void tuChoiThanhToan(UUID hocPhiId) {
        com.university.entity.HocPhi hocPhi = hocPhiAdminRepository.findById(hocPhiId)
                .orElseThrow(() -> new SimpleMessageException("Học phí không tồn tại"));
        if (hocPhi.getTrangThai() != com.university.enums.HocPhiEnum.DANG_XU_LY) {
            throw new SimpleMessageException("Học phí không ở trạng thái chờ xác nhận");
        }
        if (hocPhi.getThanhToanHocPhi() != null) {
            thanhToanHocPhiAdminRepository.delete(hocPhi.getThanhToanHocPhi());
            hocPhi.setThanhToanHocPhi(null);
        }
        hocPhi.setTrangThai(com.university.enums.HocPhiEnum.CHUA_THANH_TOAN);
        hocPhiAdminRepository.save(hocPhi);
    }

    @Transactional
    public ThanhToanHocPhiAdminResponseDTO createPayment(UUID hocPhiId, ThanhToanHocPhiAdminRequestDTO request) {
        HocPhi hocPhi = hocPhiAdminRepository.findById(hocPhiId)
                .orElseThrow(() -> new SimpleMessageException("Học phí không tồn tại"));

        if (hocPhi.getTrangThai() == com.university.enums.HocPhiEnum.DA_THANH_TOAN
                && hocPhi.getThanhToanHocPhi() != null) {
            return thanhToanMapper.toResponseDTO(hocPhi.getThanhToanHocPhi());
        }

        if (hocPhi.getThanhToanHocPhi() != null) {
            throw new SimpleMessageException("Học phí này đã được thanh toán, không thể tạo thêm");
        }

        ThanhToanHocPhi entity = thanhToanMapper.toEntity(request);
        entity.setHocPhi(hocPhi);
        entity.setMaGiaoDichGateway(UUID.randomUUID().toString());

        ThanhToanHocPhi saved = thanhToanHocPhiAdminRepository.save(entity);

        hocPhi.setThanhToanHocPhi(saved);
        hocPhi.setTrangThai(com.university.enums.HocPhiEnum.DA_THANH_TOAN);
        hocPhiAdminRepository.save(hocPhi);

        return thanhToanMapper.toResponseDTO(saved);
    }

    @Transactional
    public void sendTuitionNotification(UUID hocPhiId) {
        HocPhi hocPhi = hocPhiAdminRepository.findById(hocPhiId)
                .orElseThrow(() -> new SimpleMessageException("Học phí không tồn tại"));

        if (hocPhi.getHocVien() == null || hocPhi.getHocVien().getUsers() == null) {
            throw new SimpleMessageException("Không tìm thấy thông tin học viên");
        }

        String email = hocPhi.getHocVien().getUsers().getEmail();
        String rateKey = "rate_limit:tuition_notify:" + hocPhiId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateKey))) {
            throw new IllegalStateException("Đã gửi thông báo gần đây, vui lòng chờ sau ít phút");
        }

        // 1. Tạo thông báo trong hệ thống TRƯỚC — đây là chức năng chính
        UUID studentUsersId = hocPhi.getHocVien().getUsers().getId();
        UUID senderId = SecurityUtils.getCurrentUserId();
        String tenHocKi = hocPhi.getHocKi() != null ? hocPhi.getHocKi().getTenHocKi() : "";
        String soTienStr = String.format("%,.0f", hocPhi.getSoTien());

        NotificationRequest notifRequest = new NotificationRequest();
        notifRequest.setTieuDe("Nhắc nhở học phí " + tenHocKi);
        notifRequest.setNoiDung("Bạn có học phí kỳ " + tenHocKi + " chưa thanh toán với số tiền "
                + soTienStr + " VND. Vui lòng thanh toán trước khi kỳ học kết thúc.");
        notifRequest.setLoaiThongBao(LoaiThongBaoEnum.THONG_BAO_HOC_PHI);
        notifRequest.setUserIds(List.of(studentUsersId));
        notificationService.sendNotification(notifRequest, senderId);

        // 2. Đặt rate limit
        redisTemplate.opsForValue().set(rateKey, "1", RATE_LIMIT_TTL);

        // 3. Gửi email — tùy chọn, thất bại không ảnh hưởng thông báo trong hệ thống
        if (email != null) {
            try {
                String token = generateSecureToken();
                redisTemplate.opsForValue().set(NOTIFY_PREFIX + token, hocPhiId.toString(), NOTIFY_TTL);
                sendGridMailService.sendTuitionNotificationEmail(email, token,
                        hocPhi.getHocVien().getUsers().getHoTen(), hocPhi.getSoTien(), hocPhi.getId().toString());
            } catch (Exception ex) {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                log.warn("Gửi email nhắc học phí thất bại hocPhiId={}: {} | cause: {}", hocPhiId, ex.getMessage(), cause.getMessage());
            }
        }
    }

    public int sendTuitionNotificationBulk(List<UUID> ids) {
        int sent = 0;
        for (UUID id : ids) {
            try {
                sendTuitionNotification(id);
                sent++;
            } catch (Exception ignored) {
            }
        }
        return sent;
    }

    @Transactional
    public int sendTuitionNotificationToUser(UUID usersId) {
        if (usersId == null)
            throw new SimpleMessageException("usersId không được để trống");

        HocVien hv = hocVienAdminRepository.findByUsersId(usersId)
                .orElseThrow(() -> new SimpleMessageException("Không tìm thấy học viên tương ứng với usersId"));

        List<HocPhi> dues = hv.getDHocPhis().stream()
                .filter(hp -> hp.getTrangThai() == com.university.enums.HocPhiEnum.CHUA_THANH_TOAN
                        || hp.getTrangThai() == com.university.enums.HocPhiEnum.QUA_HAN)
                .toList();

        int sent = 0;
        for (HocPhi hp : dues) {
            try {
                sendTuitionNotification(hp.getId());
                sent++;
            } catch (Exception ignored) {
            }
        }
        return sent;
    }

    @Transactional(readOnly = true)
    public HocPhi verifyToken(String token) {
        String key = NOTIFY_PREFIX + token;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null || value.isBlank()) {
            throw new SimpleMessageException("Token không hợp lệ hoặc đã hết hạn");
        }

        UUID hocPhiId;
        try {
            hocPhiId = UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new SimpleMessageException("Token không hợp lệ hoặc đã hết hạn");
        }

        return hocPhiAdminRepository.findById(hocPhiId)
                .orElseThrow(() -> new SimpleMessageException("Học phí không tồn tại"));
    }

    private AccountingHocPhiResponse toDto(HocPhi hp) {
        AccountingHocPhiResponse dto = AccountingHocPhiResponse.builder()
                .id(hp.getId())
                .soTien(hp.getSoTien())
                .trangThai(hp.getTrangThai())
                .soTinChi(hp.getSoTinChi())
                .createdAt(hp.getCreatedAt())
                .updatedAt(hp.getUpdatedAt())
                .build();

        if (hp.getHocVien() != null) {
            dto.setHocVienId(hp.getHocVien().getId());
            dto.setMaHocVien(hp.getHocVien().getMaHocVien());
            if (hp.getHocVien().getUsers() != null) {
                dto.setHocVienName(hp.getHocVien().getUsers().getHoTen());
                dto.setHocVienEmail(hp.getHocVien().getUsers().getEmail());
            }
        }

        if (hp.getHocKi() != null) {
            dto.setHocKiId(hp.getHocKi().getId());
            dto.setHocKiMa(hp.getHocKi().getMaHocKi());
            dto.setHocKiName(hp.getHocKi().getTenHocKi());
        }

        if (hp.getThanhToanHocPhi() != null) {
            com.university.entity.ThanhToanHocPhi tt = hp.getThanhToanHocPhi();
            dto.setNgayThanhToan(tt.getNgayThanhToan());
            dto.setPhuongThucThanhToan(tt.getPhuongThucThanhToan());
            dto.setFileChungTu(tt.getFileChungTu());
            dto.setMaGiaoDich(tt.getMaGiaoDichGateway());
        }

        return dto;
    }

    private List<AccountingInvoiceCandidateResponse> buildInvoiceCandidates(UUID hocKiId) {
        Map<UUID, HocPhi> existingInvoices = hocPhiAdminRepository.findAllByHocKiId(hocKiId).stream()
                .filter(item -> item.getHocVien() != null)
                .collect(Collectors.toMap(
                        item -> item.getHocVien().getId(),
                        item -> item,
                        this::latestInvoice));

        return dangKyTinChiAdminRepository.findInvoiceCandidatesByHocKi(hocKiId).stream()
                .map(item -> {
                    HocPhi existing = existingInvoices.get(item.getHocVienId());
                    int tongSoTinChi = Optional.ofNullable(item.getTongSoTinChi()).orElse(0L).intValue();
                    return AccountingInvoiceCandidateResponse.builder()
                            .hocVienId(item.getHocVienId())
                            .maHocVien(item.getMaHocVien())
                            .hocVienName(item.getHocVienName())
                            .hocVienEmail(item.getHocVienEmail())
                            .tongSoTinChi(tongSoTinChi)
                            .soTien(tongSoTinChi * DON_GIA_TIN_CHI)
                            .daCoHoaDon(existing != null)
                            .hocPhiId(existing != null ? existing.getId() : null)
                            .trangThaiHocPhi(existing != null ? existing.getTrangThai() : null)
                            .build();
                })
                .toList();
    }

    private AccountingInvoiceGenerationResponse buildInvoiceResponse(
            HocKi hocKi,
            List<AccountingInvoiceCandidateResponse> items,
            int soHoaDonDaTao,
            int soHoaDonBoQua,
            double tongTienDaTao) {
        return AccountingInvoiceGenerationResponse.builder()
                .hocKiId(hocKi.getId())
                .hocKiMa(hocKi.getMaHocKi())
                .hocKiName(hocKi.getTenHocKi())
                .tongHocVien(items.size())
                .soHoaDonDaTao(soHoaDonDaTao)
                .soHoaDonBoQua(soHoaDonBoQua)
                .tongTienDaTao(tongTienDaTao)
                .items(items)
                .build();
    }

    private HocPhi latestInvoice(HocPhi first, HocPhi second) {
        LocalDateTime firstCreatedAt = first.getCreatedAt();
        LocalDateTime secondCreatedAt = second.getCreatedAt();
        if (firstCreatedAt == null) {
            return second;
        }
        if (secondCreatedAt == null) {
            return first;
        }
        return secondCreatedAt.isAfter(firstCreatedAt) ? second : first;
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
