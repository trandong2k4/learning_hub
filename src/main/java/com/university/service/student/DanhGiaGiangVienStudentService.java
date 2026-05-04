package com.university.service.student;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.config.SecurityUtils;
import com.university.dto.request.student.DanhGiaGiangVienStudentRequest;
import com.university.dto.response.student.DanhGiaGiangVienStudentResponse;
import com.university.entity.DangKyTinChi;
import com.university.entity.DanhGiaGiangVien;
import com.university.entity.GiangDay;
import com.university.entity.LopHocPhan;
import com.university.enums.TrangThaiLHP;
import com.university.exception.ResourceNotFoundException;
import com.university.repository.student.DangKyTinChiRepository;
import com.university.repository.student.DanhGiaGiangVienStudentRepository;
import com.university.repository.student.GiangDayStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
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

    @Transactional(readOnly = true)
    public List<DanhGiaGiangVienStudentResponse> getDanhSachDanhGia() {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        List<DangKyTinChi> completedCourses = getCompletedCourses(hocVienId);

        if (completedCourses.isEmpty()) {
            return List.of();
        }

        Map<UUID, GiangDay> giangVienByLopHocPhan = giangDayStudentRepository.findByLopHocPhanIds(
                        completedCourses.stream().map(dk -> dk.getLopHocPhan().getId()).toList())
                .stream()
                .collect(Collectors.toMap(
                        gd -> gd.getLopHocPhan().getId(),
                        Function.identity(),
                        this::pickPreferredGiangVien));

        return completedCourses.stream()
                .map(DangKyTinChi::getLopHocPhan)
                .filter(lhp -> giangVienByLopHocPhan.containsKey(lhp.getId()))
                .sorted(Comparator.comparing(LopHocPhan::getMaLopHocPhan))
                .map(lhp -> toResponse(
                        hocVienId,
                        lhp,
                        giangVienByLopHocPhan.get(lhp.getId()),
                        readDraft(hocVienId, lhp.getId())))
                .toList();
    }

    public DanhGiaGiangVienStudentResponse saveDraft(DanhGiaGiangVienStudentRequest request) {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        LopHocPhan lopHocPhan = validateEligibleCourse(hocVienId, request.getLopHocPhanId());
        GiangDay giangDay = getAssignedGiangVien(lopHocPhan.getId());

        if (isSubmitted(hocVienId, lopHocPhan.getId())) {
            throw new IllegalStateException("Môn học này đã được đánh giá");
        }

        DraftPayload draft = new DraftPayload(request.getDiemDanhGia(), request.getNhanXet().trim());
        writeDraft(hocVienId, lopHocPhan.getId(), draft);

        return toResponse(hocVienId, lopHocPhan, giangDay, draft);
    }

    public DanhGiaGiangVienStudentResponse submit(DanhGiaGiangVienStudentRequest request) {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        LopHocPhan lopHocPhan = validateEligibleCourse(hocVienId, request.getLopHocPhanId());
        GiangDay giangDay = getAssignedGiangVien(lopHocPhan.getId());

        if (isSubmitted(hocVienId, lopHocPhan.getId())) {
            throw new IllegalStateException("Môn học này đã được đánh giá");
        }

        DanhGiaGiangVien danhGia = new DanhGiaGiangVien();
        danhGia.setLopHocPhan(lopHocPhan);
        danhGia.setNhanVien(giangDay.getNhanVien());
        danhGia.setDiemDanhGia(request.getDiemDanhGia().floatValue());
        danhGia.setNhanXet(request.getNhanXet().trim());
        danhGiaGiangVienStudentRepository.save(danhGia);

        markSubmitted(hocVienId, lopHocPhan.getId());
        clearDraft(hocVienId, lopHocPhan.getId());

        return toResponse(hocVienId, lopHocPhan, giangDay, new DraftPayload(
                request.getDiemDanhGia(),
                request.getNhanXet().trim()));
    }

    private List<DangKyTinChi> getCompletedCourses(UUID hocVienId) {
        return dangKyTinChiRepository.findByHocVienIdAndTrangThai(hocVienId, TrangThaiLHP.DA_KET_THUC);
    }

    private LopHocPhan validateEligibleCourse(UUID hocVienId, UUID lopHocPhanId) {
        DangKyTinChi dangKy = getCompletedCourses(hocVienId).stream()
                .filter(item -> item.getLopHocPhan().getId().equals(lopHocPhanId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bạn chỉ được đánh giá giảng viên của môn đã hoàn thành"));

        LopHocPhan lopHocPhan = dangKy.getLopHocPhan();
        validateEvaluationWindow(lopHocPhan);
        return lopHocPhan;
    }

    private void validateEvaluationWindow(LopHocPhan lopHocPhan) {
        LocalDateTime start = getEvaluationOpenTime(lopHocPhan);
        LocalDateTime end = getEvaluationCloseTime(lopHocPhan);
        LocalDateTime now = LocalDateTime.now();

        if (start == null || end == null) {
            throw new IllegalStateException("Chưa đủ dữ liệu để mở đánh giá cho lớp học phần này");
        }
        if (now.isBefore(start) || now.isAfter(end)) {
            throw new IllegalStateException("Ngoài thời gian cho phép đánh giá");
        }
    }

    // Không đổi entity nên cửa sổ đánh giá được suy ra từ ngày kết thúc học kỳ.
    private LocalDateTime getEvaluationOpenTime(LopHocPhan lopHocPhan) {
        return lopHocPhan.getHocKi() != null ? lopHocPhan.getHocKi().getNgayKetThuc() : null;
    }

    private LocalDateTime getEvaluationCloseTime(LopHocPhan lopHocPhan) {
        LocalDateTime start = getEvaluationOpenTime(lopHocPhan);
        return start != null ? start.plusDays(EVALUATION_WINDOW_DAYS) : null;
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
        if (vaiTro == null) {
            return 0;
        }
        String normalized = vaiTro.toLowerCase();
        return (normalized.contains("chính") || normalized.contains("chinh")) ? 2 : 1;
    }

    private DanhGiaGiangVienStudentResponse toResponse(
            UUID hocVienId,
            LopHocPhan lopHocPhan,
            GiangDay giangDay,
            DraftPayload draft) {

        boolean daGui = isSubmitted(hocVienId, lopHocPhan.getId());
        LocalDateTime moDanhGia = getEvaluationOpenTime(lopHocPhan);
        LocalDateTime dongDanhGia = getEvaluationCloseTime(lopHocPhan);
        LocalDateTime now = LocalDateTime.now();
        boolean coTheDanhGia = moDanhGia != null && dongDanhGia != null
                && !now.isBefore(moDanhGia)
                && !now.isAfter(dongDanhGia)
                && !daGui;

        return DanhGiaGiangVienStudentResponse.builder()
                .lopHocPhanId(lopHocPhan.getId())
                .maLopHocPhan(lopHocPhan.getMaLopHocPhan())
                .tenMonHoc(lopHocPhan.getMonHoc().getTenMonHoc())
                .nhanVienId(giangDay.getNhanVien().getId())
                .maNhanVien(giangDay.getNhanVien().getMaNhanVien())
                .tenGiangVien(giangDay.getNhanVien().getUsers().getHoTen())
                .diemDanhGia(draft != null ? draft.diemDanhGia() : null)
                .nhanXet(draft != null ? draft.nhanXet() : null)
                .daGui(daGui)
                .coTheDanhGia(coTheDanhGia)
                .thoiGianMoDanhGia(moDanhGia)
                .thoiGianDongDanhGia(dongDanhGia)
                .build();
    }

    private DraftPayload readDraft(UUID hocVienId, UUID lopHocPhanId) {
        String raw = redisTemplate.opsForValue().get(draftKey(hocVienId, lopHocPhanId));
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(raw, DraftPayload.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Không đọc được dữ liệu nháp đánh giá");
        }
    }

    private void writeDraft(UUID hocVienId, UUID lopHocPhanId, DraftPayload draft) {
        try {
            redisTemplate.opsForValue().set(
                    draftKey(hocVienId, lopHocPhanId),
                    objectMapper.writeValueAsString(draft),
                    EVALUATION_WINDOW_DAYS,
                    TimeUnit.DAYS);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Không lưu được nháp đánh giá");
        }
    }

    private void clearDraft(UUID hocVienId, UUID lopHocPhanId) {
        redisTemplate.delete(draftKey(hocVienId, lopHocPhanId));
    }

    private boolean isSubmitted(UUID hocVienId, UUID lopHocPhanId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(submitKey(hocVienId, lopHocPhanId)));
    }

    private void markSubmitted(UUID hocVienId, UUID lopHocPhanId) {
        redisTemplate.opsForValue().set(
                submitKey(hocVienId, lopHocPhanId),
                "1",
                3650,
                TimeUnit.DAYS);
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
