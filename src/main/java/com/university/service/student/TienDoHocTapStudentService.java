package com.university.service.student;

import com.university.config.SecurityUtils;
import com.university.dto.request.student.TienDoHocTapStudentRequestDTO;
import com.university.dto.response.student.TienDoHocTapHocKyStudentResponse;
import com.university.dto.response.student.TienDoHocTapMonHocStudentResponse;
import com.university.dto.response.student.TienDoHocTapStudentResponse;
import com.university.dto.response.student.TienDoHocTapTongQuanStudentResponse;
import com.university.entity.ChuongTrinhDaoTao;
import com.university.entity.CotDiem;
import com.university.entity.DangKyTinChi;
import com.university.entity.DiemThanhPhan;
import com.university.entity.HocVien;
import com.university.entity.MonHoc;
import com.university.exception.ResourceNotFoundException;
import com.university.repository.student.HocVienStudentsRepository;
import com.university.repository.student.TienDoHocTapStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PreAuthorize("hasRole('STUDENT')")
public class TienDoHocTapStudentService {

    private static final double PASSING_SCORE = 4.0d;

    private final HocVienStudentsRepository hocVienStudentsRepository;
    private final TienDoHocTapStudentRepository tienDoHocTapStudentRepository;

    public TienDoHocTapStudentResponse getTienDoHocTap() {
        return getTienDoHocTap(new TienDoHocTapStudentRequestDTO());
    }

    public TienDoHocTapStudentResponse getTienDoHocTap(TienDoHocTapStudentRequestDTO request) {
        TienDoHocTapStudentRequestDTO safeRequest = request == null
                ? new TienDoHocTapStudentRequestDTO()
                : request;
        LearningProgressAggregate aggregate = buildAggregate(safeRequest);

        return TienDoHocTapStudentResponse.builder()
                .tongSoMonTrongChuongTrinh(aggregate.tongSoMonTrongChuongTrinh())
                .tongTinChiChuongTrinh(aggregate.tongTinChiChuongTrinh())
                .soMonDaHoc(aggregate.soMonDaHoc())
                .soMonChuaHoc(aggregate.soMonChuaHoc())
                .soTinChiDaHoanThanh(aggregate.soTinChiDaHoanThanh())
                .gpaTichLuy(aggregate.gpaTichLuy())
                .phanTramHoanThanh(aggregate.phanTramHoanThanh())
                .monDaHoc(aggregate.monDaHoc())
                .monChuaHoc(aggregate.monChuaHoc())
                .tienDoTheoHocKy(aggregate.tienDoTheoHocKy())
                .build();
    }

    public TienDoHocTapTongQuanStudentResponse getTongQuanTienDoHocTap() {
        LearningProgressAggregate aggregate = buildAggregate(new TienDoHocTapStudentRequestDTO());

        return TienDoHocTapTongQuanStudentResponse.builder()
                .tongSoMonTrongChuongTrinh(aggregate.tongSoMonTrongChuongTrinh())
                .tongTinChiChuongTrinh(aggregate.tongTinChiChuongTrinh())
                .soMonDaHoc(aggregate.soMonDaHoc())
                .soMonChuaHoc(aggregate.soMonChuaHoc())
                .soTinChiDaHoanThanh(aggregate.soTinChiDaHoanThanh())
                .gpaTichLuy(aggregate.gpaTichLuy())
                .phanTramHoanThanh(aggregate.phanTramHoanThanh())
                .build();
    }

    public List<TienDoHocTapMonHocStudentResponse> getDanhSachMonHoc(TienDoHocTapStudentRequestDTO request) {
        LearningProgressAggregate aggregate = buildAggregate(
                request == null ? new TienDoHocTapStudentRequestDTO() : request);
        List<TienDoHocTapMonHocStudentResponse> result = new ArrayList<>(aggregate.monDaHoc());
        result.addAll(aggregate.monChuaHoc());
        return result;
    }

    public List<TienDoHocTapHocKyStudentResponse> getTienDoTheoHocKy(TienDoHocTapStudentRequestDTO request) {
        return buildAggregate(request == null ? new TienDoHocTapStudentRequestDTO() : request).tienDoTheoHocKy();
    }

    private LearningProgressAggregate buildAggregate(TienDoHocTapStudentRequestDTO request) {
        LearningProgressData loadedData = loadLearningProgressData();
        ComputedLearningProgress computedData = computeLearningProgress(loadedData, request);
        return applyFilters(computedData, request);
    }

    private LearningProgressData loadLearningProgressData() {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        HocVien hocVien = hocVienStudentsRepository.findByIdWithNganh(hocVienId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hoc vien"));

        List<ChuongTrinhDaoTao> curriculum = tienDoHocTapStudentRepository
                .findCurriculumByNganhId(hocVien.getNganh().getId());
        List<DangKyTinChi> transcript = tienDoHocTapStudentRepository.findTranscriptByHocVienId(hocVienId);
        Map<UUID, List<DiemThanhPhan>> gradesByDangKyId = loadGradesByDangKyId(transcript);

        return new LearningProgressData(curriculum, transcript, gradesByDangKyId);
    }

    private ComputedLearningProgress computeLearningProgress(
            LearningProgressData loadedData,
            TienDoHocTapStudentRequestDTO request) {
        Map<UUID, TienDoMonSnapshot> bestCourseAttempts = selectBestCourseAttempts(loadedData.transcript(),
                loadedData.gradesByDangKyId());

        List<TienDoHocTapMonHocStudentResponse> monDaHoc = bestCourseAttempts.values().stream()
                .sorted(Comparator.comparing(TienDoMonSnapshot::maMonHoc))
                .map(TienDoMonSnapshot::toLearnedCourseResponse)
                .toList();

        Map<UUID, ChuongTrinhDaoTao> curriculumByCourseId = loadedData.curriculum().stream()
                .collect(Collectors.toMap(
                        ct -> ct.getMonHoc().getId(),
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new));

        List<TienDoHocTapMonHocStudentResponse> monChuaHoc = curriculumByCourseId.values().stream()
                .filter(ct -> !bestCourseAttempts.containsKey(ct.getMonHoc().getId()))
                .sorted(Comparator.comparing(ct -> ct.getMonHoc().getMaMonHoc()))
                .map(this::toRemainingCourseResponse)
                .toList();

        int tongTinChiChuongTrinh = loadedData.curriculum().stream()
                .map(ChuongTrinhDaoTao::getMonHoc)
                .map(MonHoc::getSoTinChi)
                .filter(value -> value != null)
                .mapToInt(Integer::intValue)
                .sum();

        List<TienDoHocTapHocKyStudentResponse> tienDoTheoHocKy = buildSemesterProgress(
                loadedData.transcript(),
                loadedData.gradesByDangKyId(),
                request);

        return new ComputedLearningProgress(
                curriculumByCourseId.size(),
                tongTinChiChuongTrinh,
                monDaHoc,
                monChuaHoc,
                tienDoTheoHocKy);
    }

    private LearningProgressAggregate applyFilters(
            ComputedLearningProgress computedData,
            TienDoHocTapStudentRequestDTO request) {
        List<TienDoHocTapMonHocStudentResponse> filteredMonDaHoc = computedData.monDaHoc().stream()
                .filter(item -> matchesLearnedCourse(item, request))
                .toList();

        List<TienDoHocTapMonHocStudentResponse> filteredMonChuaHoc = computedData.monChuaHoc().stream()
                .filter(item -> matchesRemainingCourse(item, request))
                .toList();

        int soTinChiDaHoanThanh = filteredMonDaHoc.stream()
                .filter(TienDoHocTapMonHocStudentResponse::isDaDat)
                .map(TienDoHocTapMonHocStudentResponse::getSoTinChi)
                .filter(value -> value != null)
                .mapToInt(Integer::intValue)
                .sum();

        Double gpaTichLuy = calculateGpa(
                filteredMonDaHoc,
                TienDoHocTapMonHocStudentResponse::getDiemHe4,
                TienDoHocTapMonHocStudentResponse::getSoTinChi);

        double phanTramHoanThanh = computedData.tongTinChiChuongTrinh() == 0
                ? 0d
                : roundToTwoDecimals((soTinChiDaHoanThanh * 100.0d) / computedData.tongTinChiChuongTrinh());

        return new LearningProgressAggregate(
                computedData.tongSoMonTrongChuongTrinh(),
                computedData.tongTinChiChuongTrinh(),
                filteredMonDaHoc.size(),
                filteredMonChuaHoc.size(),
                soTinChiDaHoanThanh,
                gpaTichLuy,
                phanTramHoanThanh,
                filteredMonDaHoc,
                filteredMonChuaHoc,
                computedData.tienDoTheoHocKy().stream()
                        .filter(item -> request.getHocKiId() == null || request.getHocKiId().equals(item.getHocKiId()))
                        .toList());
    }

    private Map<UUID, List<DiemThanhPhan>> loadGradesByDangKyId(List<DangKyTinChi> transcript) {
        List<UUID> dangKyIds = transcript.stream()
                .map(DangKyTinChi::getId)
                .toList();

        if (dangKyIds.isEmpty()) {
            return Map.of();
        }

        return tienDoHocTapStudentRepository.findGradesByDangKyIds(dangKyIds).stream()
                .collect(Collectors.groupingBy(
                        dtp -> dtp.getDangKyTinChi().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<UUID, TienDoMonSnapshot> selectBestCourseAttempts(
            List<DangKyTinChi> transcript,
            Map<UUID, List<DiemThanhPhan>> gradesByDangKyId) {

        Map<UUID, TienDoMonSnapshot> snapshots = new LinkedHashMap<>();
        for (DangKyTinChi dangKyTinChi : transcript) {
            TienDoMonSnapshot current = toSnapshot(
                    dangKyTinChi,
                    gradesByDangKyId.getOrDefault(dangKyTinChi.getId(), List.of()));
            UUID monHocId = dangKyTinChi.getLopHocPhan().getMonHoc().getId();
            snapshots.merge(monHocId, current, this::pickBetterSnapshot);
        }
        return snapshots;
    }

    private TienDoMonSnapshot pickBetterSnapshot(TienDoMonSnapshot current, TienDoMonSnapshot candidate) {
        if (current.passed() != candidate.passed()) {
            return candidate.passed() ? candidate : current;
        }
        if (current.diemTongKet() == null && candidate.diemTongKet() != null) {
            return candidate;
        }
        if (current.diemTongKet() != null && candidate.diemTongKet() == null) {
            return current;
        }
        if (current.diemTongKet() != null && candidate.diemTongKet() != null) {
            int compareScore = Double.compare(candidate.diemTongKet(), current.diemTongKet());
            if (compareScore != 0) {
                return compareScore > 0 ? candidate : current;
            }
        }
        return candidate.hocKiOrder().compareTo(current.hocKiOrder()) >= 0 ? candidate : current;
    }

    private List<TienDoHocTapHocKyStudentResponse> buildSemesterProgress(
            List<DangKyTinChi> transcript,
            Map<UUID, List<DiemThanhPhan>> gradesByDangKyId,
            TienDoHocTapStudentRequestDTO request) {

        Map<UUID, List<TienDoMonSnapshot>> snapshotsBySemester = new LinkedHashMap<>();
        for (DangKyTinChi dangKyTinChi : transcript) {
            TienDoMonSnapshot snapshot = toSnapshot(
                    dangKyTinChi,
                    gradesByDangKyId.getOrDefault(dangKyTinChi.getId(), List.of()));
            snapshotsBySemester.computeIfAbsent(
                    dangKyTinChi.getLopHocPhan().getHocKi().getId(),
                    ignored -> new ArrayList<>())
                    .add(snapshot);
        }

        return snapshotsBySemester.values().stream()
                .map(snapshots -> toSemesterResponse(snapshots, request))
                .toList();
    }

    private TienDoHocTapHocKyStudentResponse toSemesterResponse(
            List<TienDoMonSnapshot> snapshots,
            TienDoHocTapStudentRequestDTO request) {
        TienDoMonSnapshot first = snapshots.getFirst();
        List<TienDoHocTapMonHocStudentResponse> monHoc = snapshots.stream()
                .filter(snapshot -> matchesLearnedSnapshot(snapshot, request))
                .sorted(Comparator.comparing(TienDoMonSnapshot::maMonHoc))
                .map(TienDoMonSnapshot::toLearnedCourseResponse)
                .toList();

        int tongTinChi = snapshots.stream()
                .map(TienDoMonSnapshot::soTinChi)
                .filter(value -> value != null)
                .mapToInt(Integer::intValue)
                .sum();

        int tinChiHoanThanh = snapshots.stream()
                .filter(TienDoMonSnapshot::passed)
                .map(TienDoMonSnapshot::soTinChi)
                .filter(value -> value != null)
                .mapToInt(Integer::intValue)
                .sum();

        double phanTram = tongTinChi == 0
                ? 0d
                : roundToTwoDecimals((tinChiHoanThanh * 100.0d) / tongTinChi);

        return TienDoHocTapHocKyStudentResponse.builder()
                .hocKiId(first.hocKiId())
                .maHocKi(first.maHocKi())
                .tenHocKi(first.tenHocKi())
                .tongTinChi(tongTinChi)
                .tinChiHoanThanh(tinChiHoanThanh)
                .gpaHocKy(calculateGpa(snapshots, TienDoMonSnapshot::diemHe4, TienDoMonSnapshot::soTinChi))
                .phanTramHoanThanhHocKy(phanTram)
                .monHoc(monHoc)
                .build();
    }

    private TienDoMonSnapshot toSnapshot(DangKyTinChi dangKyTinChi, List<DiemThanhPhan> diemThanhPhans) {
        var lopHocPhan = dangKyTinChi.getLopHocPhan();
        var monHoc = lopHocPhan.getMonHoc();
        var hocKi = lopHocPhan.getHocKi();
        Double finalScore = calculateFinalScore(diemThanhPhans);
        boolean daHoanThanh = finalScore != null;
        boolean daDat = daHoanThanh && finalScore >= PASSING_SCORE;

        return new TienDoMonSnapshot(
                monHoc.getId(),
                monHoc.getMaMonHoc(),
                monHoc.getTenMonHoc(),
                monHoc.getSoTinChi(),
                hocKi.getId(),
                hocKi.getMaHocKi(),
                hocKi.getTenHocKi(),
                finalScore,
                finalScore == null ? null : convertToGpa4(finalScore),
                daHoanThanh,
                daDat,
                buildHocKiOrder(dangKyTinChi));
    }

    private String buildHocKiOrder(DangKyTinChi dangKyTinChi) {
        String maHocKi = dangKyTinChi.getLopHocPhan().getHocKi().getMaHocKi();
        return (maHocKi == null ? "" : maHocKi).toUpperCase(Locale.ROOT);
    }

    private Double calculateFinalScore(List<DiemThanhPhan> diemThanhPhans) {
        if (diemThanhPhans == null || diemThanhPhans.isEmpty()) {
            return null;
        }

        Map<UUID, DiemThanhPhan> latestByColumn = new LinkedHashMap<>();
        for (DiemThanhPhan diemThanhPhan : diemThanhPhans) {
            latestByColumn.putIfAbsent(diemThanhPhan.getCotDiem().getId(), diemThanhPhan);
        }

        double weightedSum = 0d;
        double totalWeight = 0d;
        for (DiemThanhPhan diemThanhPhan : latestByColumn.values()) {
            if (diemThanhPhan.getDiemSo() == null) {
                continue;
            }
            double weight = parseWeight(diemThanhPhan.getCotDiem());
            if (weight <= 0d) {
                continue;
            }
            weightedSum += diemThanhPhan.getDiemSo() * weight;
            totalWeight += weight;
        }

        if (totalWeight <= 0d) {
            return null;
        }

        return roundToTwoDecimals(weightedSum / totalWeight);
    }

    private double parseWeight(CotDiem cotDiem) {
        if (cotDiem == null || cotDiem.getTiTrong() == null || cotDiem.getTiTrong().isBlank()) {
            return 0d;
        }

        String normalized = cotDiem.getTiTrong().trim().replace("%", "").replace(",", ".");
        try {
            double parsed = Double.parseDouble(normalized);
            return parsed > 1d ? parsed / 100d : parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Du lieu diem khong hop le: ti trong cot diem khong dung");
        }
    }

    private <T> Double calculateGpa(
            List<T> items,
            Function<T, Double> diemHe4Extractor,
            Function<T, Integer> soTinChiExtractor) {
        double tongDiem = 0d;
        int tongTinChi = 0;

        for (T item : items) {
            Double diemHe4 = diemHe4Extractor.apply(item);
            Integer soTinChi = soTinChiExtractor.apply(item);
            if (diemHe4 == null || soTinChi == null || soTinChi <= 0) {
                continue;
            }
            tongDiem += diemHe4 * soTinChi;
            tongTinChi += soTinChi;
        }

        if (tongTinChi == 0) {
            return 0d;
        }

        return roundToTwoDecimals(tongDiem / tongTinChi);
    }

    private boolean matchesLearnedCourse(
            TienDoHocTapMonHocStudentResponse item,
            TienDoHocTapStudentRequestDTO request) {
        return matchesKeyword(item.getMaMonHoc(), item.getTenMonHoc(), request)
                && (request.getHocKiId() == null || request.getHocKiId().equals(item.getHocKiId()))
                && (request.getDaHoanThanh() == null || request.getDaHoanThanh().equals(item.isDaHoanThanh()))
                && (request.getDaDat() == null || request.getDaDat().equals(item.isDaDat()));
    }

    private boolean matchesLearnedSnapshot(
            TienDoMonSnapshot snapshot,
            TienDoHocTapStudentRequestDTO request) {
        return matchesKeyword(snapshot.maMonHoc(), snapshot.tenMonHoc(), request)
                && (request.getHocKiId() == null || request.getHocKiId().equals(snapshot.hocKiId()))
                && (request.getDaHoanThanh() == null || request.getDaHoanThanh().equals(snapshot.daHoanThanh()))
                && (request.getDaDat() == null || request.getDaDat().equals(snapshot.passed()));
    }

    private boolean matchesRemainingCourse(
            TienDoHocTapMonHocStudentResponse item,
            TienDoHocTapStudentRequestDTO request) {
        if (request.getHocKiId() != null) {
            return false;
        }
        if (Boolean.TRUE.equals(request.getDaHoanThanh()) || Boolean.TRUE.equals(request.getDaDat())) {
            return false;
        }
        return matchesKeyword(item.getMaMonHoc(), item.getTenMonHoc(), request);
    }

    private boolean matchesKeyword(
            String maMonHoc,
            String tenMonHoc,
            TienDoHocTapStudentRequestDTO request) {
        if (request.getKeyword() == null || request.getKeyword().isBlank()) {
            return true;
        }
        String keyword = request.getKeyword().trim().toLowerCase(Locale.ROOT);
        String normalizedMaMonHoc = maMonHoc == null ? "" : maMonHoc.toLowerCase(Locale.ROOT);
        String normalizedTenMonHoc = tenMonHoc == null ? "" : tenMonHoc.toLowerCase(Locale.ROOT);
        return normalizedMaMonHoc.contains(keyword) || normalizedTenMonHoc.contains(keyword);
    }

    private double convertToGpa4(double score10) {
        if (score10 >= 8.5d) {
            return 4.0d;
        }
        if (score10 >= 8.0d) {
            return 3.5d;
        }
        if (score10 >= 7.0d) {
            return 3.0d;
        }
        if (score10 >= 6.5d) {
            return 2.5d;
        }
        if (score10 >= 5.5d) {
            return 2.0d;
        }
        if (score10 >= 5.0d) {
            return 1.5d;
        }
        if (score10 >= 4.0d) {
            return 1.0d;
        }
        return 0d;
    }

    private TienDoHocTapMonHocStudentResponse toRemainingCourseResponse(ChuongTrinhDaoTao chuongTrinhDaoTao) {
        return TienDoHocTapMonHocStudentResponse.builder()
                .monHocId(chuongTrinhDaoTao.getMonHoc().getId())
                .hocKiId(null)
                .maMonHoc(chuongTrinhDaoTao.getMonHoc().getMaMonHoc())
                .tenMonHoc(chuongTrinhDaoTao.getMonHoc().getTenMonHoc())
                .soTinChi(chuongTrinhDaoTao.getMonHoc().getSoTinChi())
                .daHoanThanh(false)
                .daDat(false)
                .build();
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0d) / 100.0d;
    }

    private record TienDoMonSnapshot(
            UUID monHocId,
            String maMonHoc,
            String tenMonHoc,
            Integer soTinChi,
            UUID hocKiId,
            String maHocKi,
            String tenHocKi,
            Double diemTongKet,
            Double diemHe4,
            boolean daHoanThanh,
            boolean passed,
            String hocKiOrder) {

        private TienDoHocTapMonHocStudentResponse toLearnedCourseResponse() {
            return TienDoHocTapMonHocStudentResponse.builder()
                    .monHocId(monHocId)
                    .hocKiId(hocKiId)
                    .maMonHoc(maMonHoc)
                    .tenMonHoc(tenMonHoc)
                    .soTinChi(soTinChi)
                    .maHocKi(maHocKi)
                    .tenHocKi(tenHocKi)
                    .diemTongKet(diemTongKet)
                    .diemHe4(diemHe4)
                    .daHoanThanh(daHoanThanh)
                    .daDat(passed)
                    .build();
        }
    }

    private record LearningProgressAggregate(
            Integer tongSoMonTrongChuongTrinh,
            Integer tongTinChiChuongTrinh,
            Integer soMonDaHoc,
            Integer soMonChuaHoc,
            Integer soTinChiDaHoanThanh,
            Double gpaTichLuy,
            Double phanTramHoanThanh,
            List<TienDoHocTapMonHocStudentResponse> monDaHoc,
            List<TienDoHocTapMonHocStudentResponse> monChuaHoc,
            List<TienDoHocTapHocKyStudentResponse> tienDoTheoHocKy) {
    }

    private record LearningProgressData(
            List<ChuongTrinhDaoTao> curriculum,
            List<DangKyTinChi> transcript,
            Map<UUID, List<DiemThanhPhan>> gradesByDangKyId) {
    }

    private record ComputedLearningProgress(
            Integer tongSoMonTrongChuongTrinh,
            Integer tongTinChiChuongTrinh,
            List<TienDoHocTapMonHocStudentResponse> monDaHoc,
            List<TienDoHocTapMonHocStudentResponse> monChuaHoc,
            List<TienDoHocTapHocKyStudentResponse> tienDoTheoHocKy) {
    }
}
