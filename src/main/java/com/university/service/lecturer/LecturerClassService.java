package com.university.service.lecturer;

import com.university.dto.response.lecturer.LecturerClassDetailResponseDTO;
import com.university.dto.response.lecturer.LecturerClassStudentResponseDTO;
import com.university.dto.response.lecturer.LecturerClassSummaryResponseDTO;
import com.university.entity.Lich;
import com.university.entity.LopHocPhan;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.lecturer.LecturerScheduleRepository;
import com.university.repository.lecturer.LecturerStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LecturerClassService {

    private final LecturerScheduleRepository scheduleRepository;
    private final LecturerStudentRepository studentRepository;
    private final LopHocPhanAdminRepository lopHocPhanRepository;
    private final LecturerValidationService validationService;

    public List<LecturerClassSummaryResponseDTO> getClasses(UUID userId) {
        validationService.validateLecturerAssignment(userId, null);
        List<Lich> schedules = scheduleRepository.findByLopHocPhan_DGiangDays_NhanVien_Users_Id(userId);
        Map<UUID, LecturerClassSummaryResponseDTO> map = new HashMap<>();

        schedules.forEach(lich -> {
            LopHocPhan lopHocPhan = lich.getLopHocPhan();
            map.compute(lopHocPhan.getId(), (id, existing) -> {
                LocalDateTime start = lich.getNgayHoc();
                LocalDateTime end = lich.getNgayHoc();
                if (existing != null) {
                    start = existing.getNgayBatDau().isBefore(start) ? existing.getNgayBatDau() : start;
                    end = existing.getNgayKetThuc().isAfter(end) ? existing.getNgayKetThuc() : end;
                }
                return new LecturerClassSummaryResponseDTO(
                        lopHocPhan.getId(),
                        lopHocPhan.getMaLopHocPhan(),
                        lopHocPhan.getMonHoc().getTenMonHoc(),
                        lich.getPhong().getTenPhong(),
                        lich.getPhong().getToaNha(),
                        start,
                        end
                );
            });
        });

        return new ArrayList<>(map.values());
    }

    public LecturerClassDetailResponseDTO getClassDetail(UUID userId, UUID lopHocPhanId, String keyword) {
        validationService.validateLecturerAssignment(userId, lopHocPhanId);
        LopHocPhan lopHocPhan = lopHocPhanRepository.findById(lopHocPhanId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần."));

        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        List<LecturerClassStudentResponseDTO> students = studentRepository.findStudentsByLopHocPhanId(lopHocPhanId, normalizedKeyword);
        String lichMoTa = lopHocPhan.getDLichs().stream()
                .map(lich -> lich.getNgayHoc() + " - " + lich.getGioHoc().getTenGioHoc())
                .collect(Collectors.joining("; "));

        return new LecturerClassDetailResponseDTO(
                lopHocPhan.getId(),
                lopHocPhan.getMaLopHocPhan(),
                lopHocPhan.getMonHoc().getTenMonHoc(),
                validationService.firstPhong(lopHocPhan),
                validationService.firstToaNha(lopHocPhan),
                lichMoTa,
                students
        );
    }
}
