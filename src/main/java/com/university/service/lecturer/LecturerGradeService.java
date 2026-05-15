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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

        public CotDiem createCotDiem(UUID userId, CreateCotDiemRequestDTO request) {
                LopHocPhan lopHocPhan = lopHocPhanRepository.findById(UUID.fromString(request.getLopHocPhanId()))
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần."));
                validationService.validateLecturerAssignment(userId, lopHocPhan.getId());

                CotDiem cotDiem = new CotDiem();
                cotDiem.setTenCotDiem(request.getTenCotDiem());
                cotDiem.setTiTrong(request.getTiTrong());
                cotDiem.setLoai(request.getLoai());
                cotDiem.setThuTuHienThi(request.getThuTuHienThi() != null ? request.getThuTuHienThi() : 1);
                cotDiem.setLopHocPhan(lopHocPhan);
                return gradeRepository.save(cotDiem);
        }

        public GradeResponseDTO getGrades(UUID lopHocPhanId, UUID userId) {
                validationService.validateLecturerAssignment(userId, lopHocPhanId);

                List<CotDiem> cotDiems = gradeRepository.findByLopHocPhan_Id(lopHocPhanId);
                List<GradeColumnDTO> columns = cotDiems.stream()
                                .map(cd -> new GradeColumnDTO(cd.getId(), cd.getTenCotDiem(), cd.getTiTrong(),
                                                cd.getLoai() != null ? cd.getLoai().name() : null,
                                                cd.getThuTuHienThi()))
                                .collect(Collectors.toList());

                List<DiemThanhPhan> allGrades = diemThanhPhanRepository.findByDangKyTinChi_LopHocPhan_Id(lopHocPhanId);
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
                                        Float diemTrungBinh = validationService.findAverageGrade(lopHocPhanId,
                                                        hocVien.getId());
                                        return new GradeStudentResponseDTO(hocVien.getId(),
                                                        hocVien.getUsers().getHoTen(),
                                                        hocVien.getMaHocVien(), diemTrungBinh, diemThanhPhan);
                                })
                                .collect(Collectors.toList());

                return new GradeResponseDTO(lopHocPhanId, columns, students);
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

                studentGrades.forEach((key, diem) -> {
                        if (diem == null)
                                return;
                        String[] parts = key.split("\\|");
                        if (parts.length != 2)
                                return;
                        UUID hocVienId = UUID.fromString(parts[0]);
                        UUID cotDiemId = UUID.fromString(parts[1]);

                        if (!cotDiems.stream().anyMatch(cd -> cd.getId().equals(cotDiemId)))
                                return;

                        DangKyTinChi registration = dangKyTinChiRepository
                                        .findByHocVien_IdAndLopHocPhan_Id(hocVienId, lopHocPhanId)
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Sinh viên không ghi danh hoặc không thuộc lớp."));
                        DiemThanhPhan diemThanhPhan = diemThanhPhanRepository
                                        .findByDangKyTinChi_HocVien_IdAndCotDiem_Id(hocVienId, cotDiemId)
                                        .orElseGet(() -> {
                                                DiemThanhPhan newEntity = new DiemThanhPhan();
                                                newEntity.setDangKyTinChi(registration);
                                                newEntity.setCotDiem(cotDiems.stream()
                                                                .filter(cd -> cd.getId().equals(cotDiemId)).findFirst()
                                                                .get());
                                                newEntity.setLanNhap(0);
                                                return newEntity;
                                        });

                        Float diemCu = diemThanhPhan.getDiemSo();
                        diemThanhPhan.setDiemSo(diem);
                        diemThanhPhan.setLanNhap(
                                        diemThanhPhan.getLanNhap() != null ? diemThanhPhan.getLanNhap() + 1 : 1);
                        diemThanhPhan.setUpdatedAt(LocalDateTime.now());
                        diemThanhPhanRepository.save(diemThanhPhan);

                        LichSuDiem history = new LichSuDiem();
                        history.setDiemCu(diemCu != null ? diemCu : 0f);
                        history.setDiemMoi(diem);
                        history.setThoiGianThayDoi(LocalDateTime.now());
                        history.setNguoiThayDoi(lecturer);
                        history.setDiemThanhPhan(diemThanhPhan);
                        lichSuDiemRepository.save(history);

                        notificationService.sendToStudent(lecturer, registration.getHocVien().getUsers(),
                                        "Có điểm mới: " + lopHocPhan.getMonHoc().getTenMonHoc(),
                                        "Giảng viên " + lecturer.getHoTen() + " đã chấm điểm cho bạn. Điểm: " + diem);
                });
        }
}
