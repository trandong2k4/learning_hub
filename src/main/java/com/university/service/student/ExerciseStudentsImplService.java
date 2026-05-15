package com.university.service.student;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.dto.response.student.ExerciseDetailResponseDTO;
import com.university.dto.response.student.ExerciseStudentsResponseDTO;
import com.university.entity.Answers;
import com.university.entity.Exercise;
import com.university.entity.ExerciseSubmitAnswer;
import com.university.entity.Questions;
import com.university.entity.SubmitExercise;
import com.university.enums.ExerciseEnum;
import com.university.repository.student.DangKyTinChiRepository;
import com.university.repository.student.ExerciseStudentsRepository;
import com.university.repository.student.SubmitExerciseStudentsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseStudentsImplService implements ExerciseStudentsService {

    private static final long HOURS_WARNING = 24;

    private final ExerciseStudentsRepository exerciseRepo;
    private final SubmitExerciseStudentsRepository submitRepo;
    private final DangKyTinChiRepository dangKyTinChiRepo;
    private final CurrentHocVienService currentHocVienService;

    @Override
    @Transactional(readOnly = true)
    public Page<ExerciseStudentsResponseDTO> getDanhSachBaiTap(
            UUID lopHocPhanId, String keyword, int page, int size) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        validateRegistered(hocVienId, lopHocPhanId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("thoiGianBatDau").descending());
        Page<Exercise> pageData = (keyword == null || keyword.isBlank())
                ? exerciseRepo.findByLopHocPhan_Id(lopHocPhanId, pageable)
                : exerciseRepo.searchByTieuDe(lopHocPhanId, keyword, pageable);

        Map<UUID, SubmitExercise> submitMap = buildSubmitMap(pageData.getContent(), hocVienId);

        return pageData.map(exercise -> toResponseDTO(exercise, submitMap));
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseDetailResponseDTO getChiTietBaiTap(UUID exerciseId) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        Objects.requireNonNull(exerciseId, "exerciseId khong duoc null");

        Exercise exercise = exerciseRepo.findDetailById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay bai tap"));

        validateRegistered(hocVienId, exercise.getLopHocPhan().getId());
        if (exercise.getThoiGianBatDau() != null && LocalDateTime.now().isBefore(exercise.getThoiGianBatDau())) {
            throw new IllegalStateException("Bài tập chưa mở. Học viên chưa thể xem chi tiết bài tập.");
        }
        SubmitExercise latestSubmit = submitRepo
                .findTopByExercise_IdAndHocVien_IdOrderByPhienThucHienDesc(exerciseId, hocVienId)
                .orElse(null);
        int attempts = submitRepo.countByExercise_IdAndHocVien_Id(exerciseId, hocVienId);
        List<SubmitExercise> history = submitRepo
                .findByExercise_IdAndHocVien_IdOrderByPhienThucHienDescCreatedAtDesc(exerciseId, hocVienId);
        return toDetailResponseDTO(exercise, latestSubmit, attempts, history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseStudentsResponseDTO> getBaiTapSapMo(UUID lopHocPhanId) {
        validateRegistered(currentHocVienService.getCurrentHocVienId(), lopHocPhanId);
        return mapExercises(exerciseRepo.findSapMoByLopHocPhanId(lopHocPhanId, LocalDateTime.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseStudentsResponseDTO> getBaiTapDangMo(UUID lopHocPhanId) {
        validateRegistered(currentHocVienService.getCurrentHocVienId(), lopHocPhanId);
        return mapExercises(exerciseRepo.findDangMoByLopHocPhanId(lopHocPhanId, LocalDateTime.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseStudentsResponseDTO> getBaiTapDaDong(UUID lopHocPhanId) {
        validateRegistered(currentHocVienService.getCurrentHocVienId(), lopHocPhanId);
        return mapExercises(exerciseRepo.findDaDongByLopHocPhanId(lopHocPhanId, LocalDateTime.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExerciseOpen(UUID exerciseId) {
        Exercise exercise = exerciseRepo.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay bai tap"));
        validateRegistered(currentHocVienService.getCurrentHocVienId(), exercise.getLopHocPhan().getId());

        LocalDateTime now = LocalDateTime.now();
        return (exercise.getThoiGianBatDau() == null || !now.isBefore(exercise.getThoiGianBatDau()))
                && (exercise.getThoiGianKetThuc() == null || !now.isAfter(exercise.getThoiGianKetThuc()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasExerciseResult(UUID exerciseId) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();

        return submitRepo
                .findTopByExercise_IdAndHocVien_IdOrderByPhienThucHienDesc(exerciseId, hocVienId)
                .map(submit -> true)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canEditExercise(UUID exerciseId) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        Exercise exercise = exerciseRepo.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay bai tap"));
        validateRegistered(hocVienId, exercise.getLopHocPhan().getId());
        int attemptLimit = exercise.getGioiHanLanLam() != null && exercise.getGioiHanLanLam() > 0
                ? exercise.getGioiHanLanLam()
                : 1;
        return isExerciseOpen(exerciseId)
                && submitRepo.countByExercise_IdAndHocVien_Id(exerciseId, hocVienId) < attemptLimit;
    }

    private List<ExerciseStudentsResponseDTO> mapExercises(List<Exercise> exercises) {
        if (exercises.isEmpty()) {
            return List.of();
        }

        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        Map<UUID, SubmitExercise> submitMap = buildSubmitMap(exercises, hocVienId);

        return exercises.stream()
                .map(exercise -> toResponseDTO(exercise, submitMap))
                .toList();
    }

    private Map<UUID, SubmitExercise> buildSubmitMap(List<Exercise> exercises, UUID hocVienId) {
        List<UUID> ids = exercises.stream()
                .map(Exercise::getId)
                .toList();

        if (ids.isEmpty()) {
            return Map.of();
        }

        return submitRepo.findByExerciseIdsAndHocVienId(ids, hocVienId).stream()
                .collect(Collectors.toMap(
                        submit -> submit.getExercise().getId(),
                        submit -> submit,
                        (current, replacement) -> current));
    }

    private ExerciseEnum getTrangThai(Exercise exercise) {
        LocalDateTime now = LocalDateTime.now();

        if (exercise.getThoiGianBatDau() != null && now.isBefore(exercise.getThoiGianBatDau())) {
            return ExerciseEnum.SAP_MO;
        }

        if (exercise.getThoiGianKetThuc() != null) {
            if (now.isAfter(exercise.getThoiGianKetThuc())) {
                return ExerciseEnum.DA_DONG;
            }

            if (now.isAfter(exercise.getThoiGianKetThuc().minusHours(HOURS_WARNING))) {
                return ExerciseEnum.SAP_HET_HAN;
            }
        }

        return ExerciseEnum.DANG_MO;
    }

    private ExerciseStudentsResponseDTO toResponseDTO(
            Exercise exercise, Map<UUID, SubmitExercise> submitMap) {
        SubmitExercise submit = submitMap.get(exercise.getId());
        Double score = submit != null ? submit.getDiem() : null;

        return ExerciseStudentsResponseDTO.builder()
                .id(exercise.getId())
                .tieude(exercise.getTieuDe())
                .moTa(exercise.getMoTa())
                .fileExerciseUrl(exercise.getFileExerciseUrl())
                .thoiGianBatDau(exercise.getThoiGianBatDau())
                .thoiGianKetThuc(exercise.getThoiGianKetThuc())
                .gioiHanLanLam(exercise.getGioiHanLanLam())
                .soLanDaLam(submit != null && submit.getPhienThucHien() != null ? submit.getPhienThucHien() : 0)
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt())
                .lopHocPhanId(exercise.getLopHocPhan() != null ? exercise.getLopHocPhan().getId() : null)
                .maLopHocPhan(exercise.getLopHocPhan() != null ? exercise.getLopHocPhan().getMaLopHocPhan() : null)
                .trangThai(getTrangThai(exercise))
                .dacoketqua(score != null)
                .diemSo(score)
                .build();
    }

    private ExerciseDetailResponseDTO toDetailResponseDTO(
            Exercise exercise,
            SubmitExercise latestSubmit,
            int attempts,
            List<SubmitExercise> history) {
        Double score = latestSubmit != null ? latestSubmit.getDiem() : null;
        Integer currentAttempt = latestSubmit != null ? latestSubmit.getPhienThucHien() : null;

        return ExerciseDetailResponseDTO.builder()
                .id(exercise.getId())
                .tieuDe(exercise.getTieuDe())
                .moTa(exercise.getMoTa())
                .fileExerciseUrl(exercise.getFileExerciseUrl())
                .thoiGianBatDau(exercise.getThoiGianBatDau())
                .thoiGianKetThuc(exercise.getThoiGianKetThuc())
                .gioiHanLanLam(exercise.getGioiHanLanLam())
                .maLopHocPhan(exercise.getLopHocPhan() != null ? exercise.getLopHocPhan().getMaLopHocPhan() : null)
                .tenMonHoc(exercise.getLopHocPhan() != null && exercise.getLopHocPhan().getMonHoc() != null
                        ? exercise.getLopHocPhan().getMonHoc().getTenMonHoc()
                        : null)
                .trangThai(getTrangThai(exercise))
                .daCoKetQua(latestSubmit != null)
                .soLanDaLam(attempts)
                .phienHienTai(currentAttempt)
                .diem(score)
                .ghiChu(latestSubmit != null ? latestSubmit.getGhiChu() : null)
                .questions(exercise.getDQuestions().stream()
                        .map(this::toQuestionDetailDTO)
                        .toList())
                .submissionHistory(history.stream()
                        .map(this::toSubmissionHistoryDTO)
                        .toList())
                .build();
    }

    private ExerciseDetailResponseDTO.ExerciseQuestionDTO toQuestionDetailDTO(Questions question) {
        return ExerciseDetailResponseDTO.ExerciseQuestionDTO.builder()
                .questionId(question.getId())
                .noiDung(question.getNoiDung())
                .loaiCauHoi(question.getLoaiCauHoi())
                .nhieuDapAn(question.getNhieuDapAn())
                .diem(question.getDiem())
                .answers(question.getDAnswers().stream()
                        .map(this::toAnswerDetailDTO)
                        .toList())
                .build();
    }

    private ExerciseDetailResponseDTO.ExerciseQuestionDTO.ExerciseAnswerDTO toAnswerDetailDTO(Answers answer) {
        return ExerciseDetailResponseDTO.ExerciseQuestionDTO.ExerciseAnswerDTO.builder()
                .answerId(answer.getId())
                .keyAnswers(answer.getKeyAnswers())
                .conText(answer.getConText())
                .build();
    }

    private ExerciseDetailResponseDTO.ExerciseSubmissionHistoryDTO toSubmissionHistoryDTO(SubmitExercise submission) {
        return ExerciseDetailResponseDTO.ExerciseSubmissionHistoryDTO.builder()
                .submissionId(submission.getId())
                .phienThucHien(submission.getPhienThucHien())
                .fileExerciseUrl(submission.getFileExerciseUrl())
                .thoiGianNop(submission.getThoiGianNop())
                .diem(submission.getDiem())
                .ghiChu(submission.getGhiChu())
                .answers(submission.getDExerciseSubmitAnswers().stream()
                        .map(this::toSubmissionAnswerDTO)
                        .toList())
                .build();
    }

    private ExerciseDetailResponseDTO.ExerciseSubmissionAnswerDTO toSubmissionAnswerDTO(ExerciseSubmitAnswer answer) {
        Answers selectedAnswer = answer.getAnswers();
        Questions question = answer.getQuestions();
        return ExerciseDetailResponseDTO.ExerciseSubmissionAnswerDTO.builder()
                .questionId(question != null ? question.getId() : null)
                .answerId(selectedAnswer != null ? selectedAnswer.getId() : null)
                .keyAnswers(selectedAnswer != null ? selectedAnswer.getKeyAnswers() : null)
                .answerText(selectedAnswer != null ? selectedAnswer.getConText() : null)
                .noiDungTuLuan(answer.getNoiDungTuLuan())
                .isCorrect(answer.getIsCorrect())
                .diemDatDuoc(answer.getDiemDatDuoc())
                .build();
    }

    private void validateRegistered(UUID hocVienId, UUID lopHocPhanId) {
        if (lopHocPhanId == null
                || !dangKyTinChiRepo.existsByHocVienIdAndLopHocPhanId(hocVienId, lopHocPhanId)) {
            throw new IllegalStateException("Học viên chưa đăng ký lớp học phần này.");
        }
    }
}
