package com.university.service.lecturer;

import com.university.dto.request.lecturer.CreateCotDiemRequestDTO;
import com.university.dto.response.lecturer.ComponentGradeEntryDTO;
import com.university.dto.response.lecturer.GradeColumnDTO;
import com.university.dto.response.lecturer.GradeResponseDTO;
import com.university.dto.response.lecturer.GradeStudentResponseDTO;
import com.university.entity.CotDiem;
import com.university.entity.DangKyTinChi;
import com.university.entity.DiemThanhPhan;
import com.university.entity.HocVien;
import com.university.entity.LichSuDiem;
import com.university.entity.LopHocPhan;
import com.university.entity.Users;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.repository.lecturer.LecturerDangKyTinChiRepository;
import com.university.repository.lecturer.LecturerDiemThanhPhanRepository;
import com.university.repository.lecturer.LecturerGradeRepository;
import com.university.repository.lecturer.LecturerLichSuDiemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LecturerGradeService {

        private final LecturerGradeRepository gradeRepository;
        private final LecturerDiemThanhPhanRepository diemThanhPhanRepository;
        private final LecturerDangKyTinChiRepository dangKyTinChiRepository;
        private final LecturerLichSuDiemRepository lichSuDiemRepository;
        private final LopHocPhanAdminRepository lopHocPhanRepository;
        private final UsersAdminRepository userRepository;
        private final LecturerNotificationService notificationService;
        private final LecturerValidationService validationService;

        public GradeColumnDTO createCotDiem(UUID userId, CreateCotDiemRequestDTO request) {
                LopHocPhan lopHocPhan = lopHocPhanRepository.findById(UUID.fromString(request.getLopHocPhanId()))
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần."));
                validationService.validateLecturerAssignment(userId, lopHocPhan.getId());

                CotDiem cotDiem = new CotDiem();
                cotDiem.setTenCotDiem(request.getTenCotDiem());
                cotDiem.setTiTrong(request.getTiTrong());
                cotDiem.setLoai(request.getLoai());
                cotDiem.setThuTuHienThi(request.getThuTuHienThi() != null ? request.getThuTuHienThi() : 1);
                cotDiem.setLopHocPhan(lopHocPhan);
                CotDiem saved = gradeRepository.save(cotDiem);
                return new GradeColumnDTO(saved.getId(), saved.getTenCotDiem(), saved.getTiTrong(),
                                saved.getLoai() != null ? saved.getLoai().name() : null,
                                saved.getThuTuHienThi());
        }

        public GradeResponseDTO getGrades(UUID lopHocPhanId, UUID userId) {
                validationService.validateLecturerAssignment(userId, lopHocPhanId);

                LopHocPhan lopHocPhan = lopHocPhanRepository.findById(lopHocPhanId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần."));

                List<CotDiem> cotDiems = gradeRepository.findByLopHocPhan_Id(lopHocPhanId);
                List<GradeColumnDTO> columns = cotDiems.stream()
                                .map(cd -> new GradeColumnDTO(cd.getId(), cd.getTenCotDiem(), cd.getTiTrong(),
                                                cd.getLoai() != null ? cd.getLoai().name() : null,
                                                cd.getThuTuHienThi()))
                                .collect(Collectors.toList());

                List<DiemThanhPhan> allGrades = diemThanhPhanRepository.findByDangKyTinChi_LopHocPhan_Id(lopHocPhanId);

                // Pre-group grades by hocVienId for in-memory average calculation (avoids N+1)
                Map<UUID, List<DiemThanhPhan>> gradesByHocVien = allGrades.stream()
                                .collect(Collectors.groupingBy(dtp -> dtp.getDangKyTinChi().getHocVien().getId()));

                List<DangKyTinChi> registrations = dangKyTinChiRepository.findByLopHocPhan_Id(lopHocPhanId);
                List<GradeStudentResponseDTO> students = registrations.stream()
                                .map(reg -> {
                                        HocVien hocVien = reg.getHocVien();
                                        List<ComponentGradeEntryDTO> diemThanhPhan = cotDiems.stream()
                                                        .map(cd -> {
                                                                DiemThanhPhan dtp = gradesByHocVien
                                                                                .getOrDefault(hocVien.getId(),
                                                                                                List.of())
                                                                                .stream()
                                                                                .filter(d -> d.getCotDiem().getId()
                                                                                                .equals(cd.getId()))
                                                                                .findFirst().orElse(null);
                                                                return new ComponentGradeEntryDTO(cd.getId(),
                                                                                cd.getTenCotDiem(), cd.getTiTrong(),
                                                                                cd.getLoai() != null
                                                                                                ? cd.getLoai().name()
                                                                                                : null,
                                                                                dtp != null ? dtp.getDiemSo() : null,
                                                                                cd.getThuTuHienThi());
                                                        })
                                                        .collect(Collectors.toList());
                                        // Compute average in-memory from pre-loaded grades
                                        Float diemTrungBinh = computeAverageGrade(
                                                        gradesByHocVien.getOrDefault(hocVien.getId(), List.of()),
                                                        cotDiems);
                                        String hoTen = hocVien.getUsers() != null ? hocVien.getUsers().getHoTen() : "";
                                        return new GradeStudentResponseDTO(hocVien.getId(),
                                                        hoTen,
                                                        hocVien.getMaHocVien(), diemTrungBinh, diemThanhPhan);
                                })
                                .collect(Collectors.toList());

                return new GradeResponseDTO(lopHocPhanId, columns, students);
        }

        /**
         * Compute weighted average from already-loaded DiemThanhPhan list.
         * Avoids per-student repository calls.
         */
        private Float computeAverageGrade(List<DiemThanhPhan> grades, List<CotDiem> allCotDiems) {
                if (grades.isEmpty()) {
                        return null;
                }
                Map<UUID, CotDiem> cotDiemMap = allCotDiems.stream()
                                .collect(Collectors.toMap(CotDiem::getId, cd -> cd));
                double weightedSum = 0d;
                double totalWeight = 0d;
                boolean hasValid = false;
                for (DiemThanhPhan grade : grades) {
                        if (grade.getDiemSo() == null) {
                                continue;
                        }
                        CotDiem cd = cotDiemMap.get(grade.getCotDiem().getId());
                        if (cd == null) {
                                continue;
                        }
                        double weight = parseWeight(cd.getTiTrong());
                        if (weight <= 0d) {
                                continue;
                        }
                        weightedSum += grade.getDiemSo() * weight;
                        totalWeight += weight;
                        hasValid = true;
                }
                if (!hasValid || totalWeight == 0d) {
                        return null;
                }
                return (float) Math.round((weightedSum / totalWeight) * 100.0d) / 100.0f;
        }

        private double parseWeight(String tiTrong) {
                if (tiTrong == null || tiTrong.isBlank()) {
                        return 0d;
                }
                String normalized = tiTrong.trim().replace("%", "").replace(",", ".");
                try {
                        double parsed = Double.parseDouble(normalized);
                        return parsed > 1d ? parsed / 100d : parsed;
                } catch (NumberFormatException ex) {
                        return 0d;
                }
        }

        public void updateGrades(UUID userId, UUID lopHocPhanId, Map<String, Float> studentGrades) {
                validationService.validateLecturerAssignment(userId, lopHocPhanId);
                Users lecturer = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User không tồn tại."));
                LopHocPhan lopHocPhan = lopHocPhanRepository.findById(lopHocPhanId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần."));

                for (Float diem : studentGrades.values()) {
                        if (diem != null && (diem < 0 || diem > 10)) {
                                throw new RuntimeException("Điểm phải nằm trong khoảng 0-10.");
                        }
                }

                List<CotDiem> cotDiems = gradeRepository.findByLopHocPhan_Id(lopHocPhanId);

                // Pre-load all DangKyTinChi and DiemThanhPhan to avoid N+1
                List<DangKyTinChi> registrations = dangKyTinChiRepository.findByLopHocPhan_Id(lopHocPhanId);
                List<DiemThanhPhan> existingGrades = diemThanhPhanRepository.findByDangKyTinChi_LopHocPhan_Id(lopHocPhanId);

                Map<UUID, DangKyTinChi> regByHocVien = registrations.stream()
                                .collect(Collectors.toMap(reg -> reg.getHocVien().getId(), reg -> reg));
                Map<String, DiemThanhPhan> gradeKeyMap = existingGrades.stream()
                                .collect(Collectors.toMap(
                                        g -> g.getDangKyTinChi().getHocVien().getId() + "|" + g.getCotDiem().getId(),
                                        g -> g));

                final UUID finalLopHocPhanId = lopHocPhanId;
                final Users finalLecturer = lecturer;
                final LopHocPhan finalLopHocPhan = lopHocPhan;
                final List<CotDiem> finalCotDiems = cotDiems;

                studentGrades.forEach((key, diem) -> {
                        String[] parts = key.split("\\|");
                        if (parts.length != 2)
                                return;
                        UUID hocVienId = UUID.fromString(parts[0]);
                        UUID cotDiemId = UUID.fromString(parts[1]);

                        if (!finalCotDiems.stream().anyMatch(cd -> cd.getId().equals(cotDiemId)))
                                return;

                        DangKyTinChi registration = regByHocVien.get(hocVienId);
                        if (registration == null)
                                return;

                        DiemThanhPhan diemThanhPhan = gradeKeyMap.get(key);
                        if (diem == null && diemThanhPhan == null)
                                return;

                        if (diemThanhPhan == null) {
                                diemThanhPhan = new DiemThanhPhan();
                                diemThanhPhan.setDangKyTinChi(registration);
                                diemThanhPhan.setCotDiem(finalCotDiems.stream()
                                                .filter(cd -> cd.getId().equals(cotDiemId)).findFirst().get());
                                diemThanhPhan.setLanNhap(0);
                        }

                        Float diemCu = diemThanhPhan.getDiemSo();
                        if (diem == null && diemCu == null)
                                return;

                        diemThanhPhan.setDiemSo(diem);
                        diemThanhPhan.setLanNhap(
                                        diemThanhPhan.getLanNhap() != null ? diemThanhPhan.getLanNhap() + 1 : 1);
                        diemThanhPhan.setUpdatedAt(LocalDateTime.now());
                        DiemThanhPhan saved = diemThanhPhanRepository.save(diemThanhPhan);

                        LichSuDiem history = new LichSuDiem();
                        history.setDiemCu(diemCu != null ? diemCu : 0f);
                        history.setDiemMoi(diem != null ? diem : 0f);
                        history.setThoiGianThayDoi(LocalDateTime.now());
                        history.setNguoiThayDoi(finalLecturer);
                        history.setDiemThanhPhan(saved);
                        lichSuDiemRepository.save(history);

                        try {
                                UUID studentUserId = registration.getHocVien().getUsers().getId();
                                notificationService.sendToStudentAsync(
                                                finalLecturer.getId(),
                                                studentUserId,
                                                "Có điểm mới: " + finalLopHocPhan.getMonHoc().getTenMonHoc(),
                                                "Giảng viên " + finalLecturer.getHoTen()
                                                                + (diem != null ? " đã chấm điểm cho bạn. Điểm: " + diem
                                                                                : " đã xóa điểm của bạn."));
                        } catch (Exception e) {
                                log.warn("Không thể gửi thông báo điểm cho học viên {}: {}", hocVienId, e.getMessage());
                        }
                });
        }
}
