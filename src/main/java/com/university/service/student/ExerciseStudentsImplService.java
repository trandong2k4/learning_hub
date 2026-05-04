package com.university.service.student;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.dto.response.student.ExerciseStudentsResponseDTO;
import com.university.entity.Exercise;
import com.university.entity.SubmitExercise;
import com.university.enums.ExerciseEnum;
import com.university.repository.student.ExerciseStudentsRepository;
import com.university.repository.student.SubmitExerciseStudentsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseStudentsImplService implements ExerciseStudentsService {

    private final ExerciseStudentsRepository exerciseRepo;
    private final SubmitExerciseStudentsRepository submitRepo;

    private static final long HOURS_WARNING = 24;

    // ================= DANH SÁCH =================
    @Override
    @Transactional(readOnly = true)
    public Page<ExerciseStudentsResponseDTO> getDanhSachBaiTap(
            UUID lopHocPhanId, UUID hocVienId, String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("thoiGianBatDau").descending());

        Page<Exercise> pageData = (keyword == null || keyword.isBlank())
                ? exerciseRepo.findByLopHocPhan_Id(lopHocPhanId, pageable)
                : exerciseRepo.searchByTieuDe(lopHocPhanId, keyword, pageable);

        Map<UUID, SubmitExercise> submitMap =
                buildSubmitMap(pageData.getContent(), hocVienId);

        return pageData.map(e -> toResponseDTO(e, submitMap));
    }

    // ================= CHI TIẾT =================
    @Override
    @Transactional(readOnly = true)
    public ExerciseStudentsResponseDTO getChiTietBaiTap(UUID exerciseId, UUID hocVienId) {

        Objects.requireNonNull(exerciseId);

        Exercise exercise = exerciseRepo.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập"));

        Map<UUID, SubmitExercise> submitMap =
                buildSubmitMap(List.of(exercise), hocVienId);

        return toResponseDTO(exercise, submitMap);
    }

    // ================= DANH SÁCH THEO TRẠNG THÁI =================

    @Override
    public List<ExerciseStudentsResponseDTO> getBaiTapSapMo(UUID lopHocPhanId, UUID hocVienId) {
        return mapExercises(
                exerciseRepo.findSapMoByLopHocPhanId(lopHocPhanId, LocalDateTime.now()),
                hocVienId
        );
    }

    @Override
    public List<ExerciseStudentsResponseDTO> getBaiTapDangMo(UUID lopHocPhanId, UUID hocVienId) {
        return mapExercises(
                exerciseRepo.findDangMoByLopHocPhanId(lopHocPhanId, LocalDateTime.now()),
                hocVienId
        );
    }

    @Override
    public List<ExerciseStudentsResponseDTO> getBaiTapDaDong(UUID lopHocPhanId, UUID hocVienId) {
        return mapExercises(
                exerciseRepo.findDaDongByLopHocPhanId(lopHocPhanId, LocalDateTime.now()),
                hocVienId
        );
    }

    // ================= HELPER =================

    private List<ExerciseStudentsResponseDTO> mapExercises(
            List<Exercise> exercises, UUID hocVienId) {

        if (exercises.isEmpty()) return List.of();

        Map<UUID, SubmitExercise> submitMap =
                buildSubmitMap(exercises, hocVienId);

        return exercises.stream()
                .map(e -> toResponseDTO(e, submitMap))
                .toList();
    }

    // ================= BUILD MAP =================

    private Map<UUID, SubmitExercise> buildSubmitMap(
            List<Exercise> exercises, UUID hocVienId) {

        List<UUID> ids = exercises.stream()
                .map(Exercise::getId)
                .toList();

        List<SubmitExercise> submits =
                submitRepo.findByExerciseIdsAndHocVienId(ids, hocVienId);

        return submits.stream()
                .collect(Collectors.toMap(
                        s -> s.getExercise().getId(),
                        s -> s
                ));
    }

    // ================= CHECK =================

    @Override
    public boolean isExerciseOpen(UUID exerciseId) {

        Exercise e = exerciseRepo.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập"));

        LocalDateTime now = LocalDateTime.now();

        return e.getThoiGianBatDau() != null
                && e.getThoiGianKetThuc() != null
                && !now.isBefore(e.getThoiGianBatDau())
                && !now.isAfter(e.getThoiGianKetThuc());
    }

    @Override
public boolean hasExerciseResult(UUID exerciseId, UUID hocVienId) {

    return submitRepo
            .findByExercise_IdAndHocVien_Id(exerciseId, hocVienId)
            .map(s -> s.getDiem() != null)
            .orElse(false);
}

    @Override
    public boolean canEditExercise(UUID exerciseId, UUID hocVienId) {

        boolean isOpen = isExerciseOpen(exerciseId);
        boolean hasResult = hasExerciseResult(exerciseId, hocVienId);

        return isOpen && !hasResult;
    }

    // ================= TRẠNG THÁI =================

    private ExerciseEnum getTrangThai(Exercise e) {

        LocalDateTime now = LocalDateTime.now();

        if (e.getThoiGianBatDau() != null && now.isBefore(e.getThoiGianBatDau())) {
            return ExerciseEnum.SAP_MO;
        }

        if (e.getThoiGianKetThuc() != null) {

            if (now.isAfter(e.getThoiGianKetThuc())) {
                return ExerciseEnum.DA_DONG;
            }

            if (now.isAfter(e.getThoiGianKetThuc().minusHours(HOURS_WARNING))) {
                return ExerciseEnum.SAP_HET_HAN;
            }
        }

        return ExerciseEnum.DANG_MO;
    }

    // ================= DTO =================

    private ExerciseStudentsResponseDTO toResponseDTO(
            Exercise e, Map<UUID, SubmitExercise> submitMap) {

        SubmitExercise submit = submitMap.get(e.getId());

        Double score = submit != null ? submit.getDiem() : null; // ✅ sửa ở đây

        return ExerciseStudentsResponseDTO.builder()
                .id(e.getId())
                .tieude(e.getTieuDe())
                .moTa(e.getMoTa())
                .thoiGianBatDau(e.getThoiGianBatDau())
                .thoiGianKetThuc(e.getThoiGianKetThuc())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .lopHocPhanId(e.getLopHocPhan() != null ? e.getLopHocPhan().getId() : null)
                .maLopHocPhan(e.getLopHocPhan() != null ? e.getLopHocPhan().getMaLopHocPhan() : null)
                .trangThai(getTrangThai(e))
                .dacoketqua(score != null)
                .diemSo(score)
                .build();
    }
}
