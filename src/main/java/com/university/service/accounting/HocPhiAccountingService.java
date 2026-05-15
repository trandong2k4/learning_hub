package com.university.service.accounting;

import com.university.config.SecurityUtils;
import com.university.dto.request.Notification.NotificationRequest;
import com.university.dto.request.admin.ThanhToanHocPhiAdminRequestDTO;
import com.university.dto.response.admin.ThanhToanHocPhiAdminResponseDTO;
import com.university.dto.response.accounting.AccountingHocPhiResponse;
import com.university.entity.HocPhi;
import com.university.entity.HocVien;
import com.university.entity.ThanhToanHocPhi;
import com.university.enums.LoaiThongBaoEnum;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.ThanhToanHocPhiAdminMapper;
import com.university.repository.admin.HocVienAdminRepository;
import com.university.repository.admin.HocPhiAdminRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HocPhiAccountingService {

    private static final String NOTIFY_PREFIX = "tuition_notify:";
    private static final Duration NOTIFY_TTL = Duration.ofDays(7);
    private static final Duration RATE_LIMIT_TTL = Duration.ofMinutes(2);

    private final HocPhiAdminRepository hocPhiAdminRepository;
    private final ThanhToanHocPhiAdminRepository thanhToanHocPhiAdminRepository;
    private final ThanhToanHocPhiAdminMapper thanhToanMapper;
    private final SendGridMailService sendGridMailService;
    private final StringRedisTemplate redisTemplate;
    private final HocVienAdminRepository hocVienAdminRepository;
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

    @Transactional
    public void xacNhanThanhToan(UUID hocPhiId) {
        com.university.entity.HocPhi hocPhi = hocPhiAdminRepository.findById(hocPhiId)
                .orElseThrow(() -> new SimpleMessageException("Học phí không tồn tại"));
        if (hocPhi.getTrangThai() != com.university.enums.HocPhiEnum.DANG_XU_LY) {
            throw new SimpleMessageException("Học phí chưa được học viên nộp chứng từ");
        }
        hocPhi.setTrangThai(com.university.enums.HocPhiEnum.DA_THANH_TOAN);
        hocPhiAdminRepository.save(hocPhi);
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
                || hocPhi.getThanhToanHocPhi() != null) {
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
            throw new SimpleMessageException("Vui lòng đợi trước khi gửi lại");
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
                log.warn("Gửi email nhắc học phí thất bại hocPhiId={}: {}", hocPhiId, ex.getMessage());
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

    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
