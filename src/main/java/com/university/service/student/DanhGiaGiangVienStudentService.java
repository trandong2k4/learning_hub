package com.university.service.student;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.dto.request.student.DanhGiaGiangVienDraftRequest;
import com.university.dto.request.student.DanhGiaGiangVienStudentRequest;
import com.university.dto.response.student.DanhGiaGiangVienStudentResponse;
import com.university.entity.DangKyTinChi;
import com.university.entity.DanhGiaGiangVien;
import com.university.entity.GiangDay;
import com.university.entity.LopHocPhan;
import com.university.exception.ResourceNotFoundException;
import com.university.repository.student.DangKyTinChiRepository;
import com.university.repository.student.DanhGiaGiangVienStudentRepository;
import com.university.repository.student.GiangDayStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DanhGiaGiangVienStudentService {

    private static final long EVALUATION_WINDOW_DAYS = 30;

    private final DangKyTinChiRepository dangKyTinChiRepository;
    private final GiangDayStudentRepository giangDayStudentRepository;
    private final DanhGiaGiangVienStudentRepository danhGiaGiangVienStudentRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CurrentHocVienService currentHocVienService;

    @Transactional(readOnly = true)
    public List<DanhGiaGiangVienStudentResponse> getDanhSachDanhGia() {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        List<DangKyTinChi> registrations = dangKyTinChiRepository.findAllByHocVienId(hocVienId);

        if (registrations.isEmpty()) {
            return List.of();
        }

        List<LopHocPhan> lopHocPhans = registrations.stream()
                .map(DangKyTinChi::getLopHocPhan)
                .toList();

        List<UUID> lopHocPhanIds = lopHocPhans.stream().map(LopHocPhan::getId).toList();

        Map<UUID, GiangDay> giangVienByLopHocPhan = giangDayStudentRepository
                .findByLopHocPhanIds(lopHocPhanIds)
                .stream()
                .collect(Collectors.toMap(
                        gd -> gd.getLopHocPhan().getId(),
                        Function.identity(),
                        this::pickPreferredGiangVien));

        // Batch Redis: 1 multiGet cho submit keys, 1 cho draft keys
        List<String> submitKeys = lopHocPhanIds.stream()
                .map(id -> submitKey(hocVienId, id)).toList();
        List<String> draftKeys = lopHocPhanIds.stream()
                .map(id -> draftKey(hocVienId, id)).toList();

        List<String> submitValues = redisTemplate.opsForValue().multiGet(submitKeys);
        List<String> draftValues = redisTemplate.opsForValue().multiGet(draftKeys);

        // Map lopHocPhanId -> index để tra O(1)
        Map<UUID, Integer> idxMap = new HashMap<>();
        for (int i = 0; i < lopHocPhanIds.size(); i++) {
            idxMap.put(lopHocPhanIds.get(i), i);
        }

        return lopHocPhans.stream()
                .filter(lhp -> giangVienByLopHocPhan.containsKey(lhp.getId()))
                .sorted(Comparator.comparing(LopHocPhan::getMaLopHocPhan))
                .<DanhGiaGiangVienStudentResponse>map(lhp -> {
                    int idx = idxMap.get(lhp.getId());
                    String submitRaw = submitValues != null ? submitValues.get(idx) : null;
                    String draftRaw = draftValues != null ? draftValues.get(idx) : null;

                    boolean daGui = submitRaw != null;
                    DraftPayload displayPayload = daGui
                            ? deserializePayload(submitRaw)
                            : deserializePayload(draftRaw);

                    return toResponse(lhp, giangVienByLopHocPhan.get(lhp.getId()), displayPayload, daGui);
                })
                .toList();
    }

    public DanhGiaGiangVienStudentResponse saveDraft(DanhGiaGiangVienDraftRequest request) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        LopHocPhan lopHocPhan = validateEligibleCourse(hocVienId, request.getLopHocPhanId());
        GiangDay giangDay = getAssignedGiangVien(lopHocPhan.getId());

        if (isSubmitted(hocVienId, lopHocPhan.getId())) {
            throw new IllegalStateException("Môn học này đã được đánh giá");
        }

        String nhanXet = request.getNhanXet() != null ? request.getNhanXet().trim() : null;
        DraftPayload draft = new DraftPayload(request.getDiemDanhGia(), nhanXet);

        // TTL = thời gian còn lại đến khi đóng cửa sổ đánh giá
        LocalDateTime dongCuaSo = lopHocPhan.getHocKi().getNgayKetThuc()
                .plusDays(EVALUATION_WINDOW_DAYS);
        long remainingSeconds = Duration.between(LocalDateTime.now(), dongCuaSo).getSeconds();
        writeDraft(hocVienId, lopHocPhan.getId(), draft, Math.max(remainingSeconds, 1));

        return toResponse(lopHocPhan, giangDay, draft, false);
    }

    public DanhGiaGiangVienStudentResponse submit(DanhGiaGiangVienStudentRequest request) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        LopHocPhan lopHocPhan = validateEligibleCourse(hocVienId, request.getLopHocPhanId());
        GiangDay giangDay = getAssignedGiangVien(lopHocPhan.getId());

        DraftPayload submittedPayload = new DraftPayload(
                request.getDiemDanhGia(), request.getNhanXet().trim());

        // Atomic SET NX: vừa chặn race condition vừa lưu payload để hiển thị sau
        Boolean claimed = redisTemplate.opsForValue().setIfAbsent(
                submitKey(hocVienId, lopHocPhan.getId()),
                serializePayload(submittedPayload),
                3650, TimeUnit.DAYS);

        if (Boolean.FALSE.equals(claimed)) {
            throw new IllegalStateException("Môn học này đã được đánh giá");
        }

        try {
            DanhGiaGiangVien danhGia = new DanhGiaGiangVien();
            danhGia.setLopHocPhan(lopHocPhan);
            danhGia.setNhanVien(giangDay.getNhanVien());
            danhGia.setDiemDanhGia(request.getDiemDanhGia().floatValue());
            danhGia.setNhanXet(request.getNhanXet().trim());
            danhGiaGiangVienStudentRepository.save(danhGia);
        } catch (Exception e) {
            // Rollback Redis nếu DB lỗi để không mất khả năng submit lại
            redisTemplate.delete(submitKey(hocVienId, lopHocPhan.getId()));
            throw e;
        }

        clearDraft(hocVienId, lopHocPhan.getId());
        return toResponse(lopHocPhan, giangDay, submittedPayload, true);
    }

    private LopHocPhan validateEligibleCourse(UUID hocVienId, UUID lopHocPhanId) {
        DangKyTinChi dangKy = dangKyTinChiRepository
                .findByHocVienIdAndLopHocPhanId(hocVienId, lopHocPhanId)
                .orElseThrow(() -> new IllegalStateException("Bạn chỉ được đánh giá giảng viên của môn đã đăng ký"));

        LopHocPhan lopHocPhan = dangKy.getLopHocPhan();

        LocalDateTime ngayKetThuc = lopHocPhan.getHocKi() != null
                ? lopHocPhan.getHocKi().getNgayKetThuc()
                : null;
        if (ngayKetThuc == null || LocalDateTime.now().isBefore(ngayKetThuc)) {
            throw new IllegalStateException("Môn học chưa kết thúc, chưa thể đánh giá giảng viên");
        }
        if (LocalDateTime.now().isAfter(ngayKetThuc.plusDays(EVALUATION_WINDOW_DAYS))) {
            throw new IllegalStateException("Đã hết thời hạn đánh giá (30 ngày kể từ ngày kết thúc học kỳ)");
        }

        return lopHocPhan;
    }

    private GiangDay getAssignedGiangVien(UUID lopHocPhanId) {
        return giangDayStudentRepository.findByLopHocPhanIds(List.of(lopHocPhanId))
                .stream()
                .reduce(this::pickPreferredGiangVien)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên của lớp học phần"));
    }

    private GiangDay pickPreferredGiangVien(GiangDay current, GiangDay candidate) {
        return scoreRole(candidate.getVaiTro()) > scoreRole(current.getVaiTro()) ? candidate : current;
    }

    private int scoreRole(String vaiTro) {
        if (vaiTro == null)
            return 0;
        String normalized = vaiTro.toLowerCase();
        return (normalized.contains("chính") || normalized.contains("chinh")) ? 2 : 1;
    }

    private DanhGiaGiangVienStudentResponse toResponse(
            LopHocPhan lopHocPhan,
            GiangDay giangDay,
            DraftPayload payload,
            boolean daGui) {

        LocalDateTime ngayKetThuc = lopHocPhan.getHocKi() != null
                ? lopHocPhan.getHocKi().getNgayKetThuc()
                : null;
        LocalDateTime ngayDongDanhGia = ngayKetThuc != null
                ? ngayKetThuc.plusDays(EVALUATION_WINDOW_DAYS)
                : null;
        LocalDateTime now = LocalDateTime.now();
        boolean coTheDanhGia = ngayKetThuc != null
                && now.isAfter(ngayKetThuc)
                && now.isBefore(ngayDongDanhGia)
                && !daGui;

        return DanhGiaGiangVienStudentResponse.builder()
                .lopHocPhanId(lopHocPhan.getId())
                .maLopHocPhan(lopHocPhan.getMaLopHocPhan())
                .tenMonHoc(lopHocPhan.getMonHoc().getTenMonHoc())
                .nhanVienId(giangDay.getNhanVien().getId())
                .maNhanVien(giangDay.getNhanVien().getMaNhanVien())
                .tenGiangVien(giangDay.getNhanVien().getUsers().getHoTen())
                .diemDanhGia(payload != null ? payload.diemDanhGia() : null)
                .nhanXet(payload != null ? payload.nhanXet() : null)
                .daGui(daGui)
                .coTheDanhGia(coTheDanhGia)
                .thoiGianMoDanhGia(ngayKetThuc)
                .thoiGianDongDanhGia(ngayDongDanhGia)
                .hocKiId(lopHocPhan.getHocKi() != null ? lopHocPhan.getHocKi().getId() : null)
                .maHocKi(lopHocPhan.getHocKi() != null ? lopHocPhan.getHocKi().getMaHocKi() : null)
                .tenHocKi(lopHocPhan.getHocKi() != null ? lopHocPhan.getHocKi().getTenHocKi() : null)
                .ngayBatDauHocKi(lopHocPhan.getHocKi() != null ? lopHocPhan.getHocKi().getNgayBatDau() : null)
                .build();
    }

    // ── Redis helpers ────────────────────────────────────────────────────────────

    private boolean isSubmitted(UUID hocVienId, UUID lopHocPhanId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(submitKey(hocVienId, lopHocPhanId)));
    }

    private DraftPayload deserializePayload(String raw) {
        if (raw == null || raw.isBlank())
            return null;
        try {
            return objectMapper.readValue(raw, DraftPayload.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    private String serializePayload(DraftPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Lỗi xử lý dữ liệu đánh giá");
        }
    }

    private void writeDraft(UUID hocVienId, UUID lopHocPhanId, DraftPayload draft, long ttlSeconds) {
        redisTemplate.opsForValue().set(
                draftKey(hocVienId, lopHocPhanId),
                serializePayload(draft),
                ttlSeconds,
                TimeUnit.SECONDS);
    }

    private void clearDraft(UUID hocVienId, UUID lopHocPhanId) {
        redisTemplate.delete(draftKey(hocVienId, lopHocPhanId));
    }

    private String draftKey(UUID hocVienId, UUID lopHocPhanId) {
        return "student-review:draft:" + hocVienId + ":" + lopHocPhanId;
    }

    private String submitKey(UUID hocVienId, UUID lopHocPhanId) {
        return "student-review:submitted:" + hocVienId + ":" + lopHocPhanId;
    }

    private record DraftPayload(Integer diemDanhGia, String nhanXet) {
    }
}
