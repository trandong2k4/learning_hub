package com.university.service.lecturer;

import com.university.dto.request.lecturer.AttendanceEntryDTO;
import com.university.dto.request.lecturer.AttendanceRequestDTO;
import com.university.dto.response.lecturer.AttendanceResponseDTO;
import com.university.dto.response.lecturer.AttendanceStudentResponseDTO;
import com.university.entity.DangKyTinChi;
import com.university.entity.DiemDanh;
import com.university.entity.HocVien;
import com.university.entity.Lich;
import com.university.entity.LopHocPhan;
import com.university.enums.TrangThaiDiemDanhEnum;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.lecturer.LecturerAttendanceRepository;
import com.university.repository.lecturer.LecturerDangKyTinChiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LecturerAttendanceService {

    private final LecturerAttendanceRepository attendanceRepository;
    private final LecturerDangKyTinChiRepository dangKyTinChiRepository;
    private final LopHocPhanAdminRepository lopHocPhanRepository;
    private final LecturerValidationService validationService;

    public AttendanceResponseDTO getAttendance(UUID lopHocPhanId, UUID userId, String lichId) {
        validationService.validateLecturerAssignment(userId, lopHocPhanId);
        LopHocPhan lopHocPhan = lopHocPhanRepository.findById(lopHocPhanId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần."));

        LocalDateTime now = LocalDateTime.now();
        List<Lich> schedule = lopHocPhan.getDLichs();
        Lich currentSession = schedule.stream()
                .filter(lich -> canTakeAttendance(lich, now))
                .findFirst()
                .orElse(null);

        String resolvedLichId = (lichId != null && !lichId.isBlank())
                ? lichId
                : (currentSession != null ? currentSession.getId().toString() : null);

        List<DiemDanh> classAttendance = attendanceRepository.findByLich_LopHocPhan_Id(lopHocPhanId);
        Map<UUID, Integer> absenceCountMap = classAttendance.stream()
                .filter(dd -> dd.getLich() != null && isScheduleNotAfterNow(dd.getLich(), now))
                .filter(dd -> dd.getTrangThai() == TrangThaiDiemDanhEnum.ABSENT
                        || dd.getTrangThai() == TrangThaiDiemDanhEnum.EXCUSED)
                .collect(Collectors.groupingBy(dd -> dd.getHocVien().getId(), Collectors.summingInt(dd -> 1)));

        List<AttendanceResponseDTO.SessionDTO> sessions = schedule.stream()
                .sorted((a, b) -> {
                    int dateCompare = a.getNgayHoc().compareTo(b.getNgayHoc());
                    if (dateCompare != 0) return dateCompare;
                    return a.getGioHoc().getThoiGianBatDau().compareTo(b.getGioHoc().getThoiGianBatDau());
                })
                .map(lich -> {
                    boolean canTake = canTakeAttendance(lich, now);
                    boolean hasAttendance = classAttendance.stream()
                            .anyMatch(dd -> dd.getLich().getId().equals(lich.getId()));
                    return new AttendanceResponseDTO.SessionDTO(
                            lich.getId().toString(),
                            lich.getNgayHoc().toLocalDate().toString(),
                            lich.getGioHoc().getThoiGianBatDau(),
                            lich.getGioHoc().getThoiGianKetThuc(),
                            lich.getPhong().getTenPhong(),
                            canTake,
                            canTake,
                            hasAttendance);
                })

                .collect(Collectors.toList());

        List<DangKyTinChi> registrations = dangKyTinChiRepository.findByLopHocPhan_Id(lopHocPhanId);

        List<AttendanceStudentResponseDTO> students;
        AttendanceResponseDTO.AttendanceStatsDTO stats;
        Map<UUID, DiemDanh> attendanceMap;
        Lich selectedSession = null;

        if (resolvedLichId != null && !resolvedLichId.isBlank()) {
            UUID lichUuid = UUID.fromString(resolvedLichId);
            selectedSession = schedule.stream()
                    .filter(l -> l.getId().equals(lichUuid))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Buổi học không thuộc lớp học phần này."));
            attendanceMap = attendanceRepository.findByLich_Id(lichUuid).stream()
                    .collect(Collectors.toMap(d -> d.getHocVien().getId(), d -> d));
            students = registrations.stream()
                    .map(reg -> {
                        DiemDanh dd = attendanceMap.get(reg.getHocVien().getId());
                        return new AttendanceStudentResponseDTO(
                                reg.getHocVien().getId(),
                                reg.getHocVien().getUsers().getHoTen(),
                                reg.getHocVien().getMaHocVien(),
                                dd != null && dd.getTrangThai() != null ? dd.getTrangThai().name() : null,
                                dd != null ? dd.getGhiChu() : null,
                                absenceCountMap.getOrDefault(reg.getHocVien().getId(), 0));
                    })
                    .collect(Collectors.toList());
            stats = buildStats(students, registrations.size());
        } else {
            attendanceMap = Map.of();
            students = registrations.stream()
                    .map(reg -> new AttendanceStudentResponseDTO(
                            reg.getHocVien().getId(),
                            reg.getHocVien().getUsers().getHoTen(),
                            reg.getHocVien().getMaHocVien(),
                            null,
                            null,
                            absenceCountMap.getOrDefault(reg.getHocVien().getId(), 0)))
                    .collect(Collectors.toList());
            stats = buildStats(students, registrations.size());
        }

        boolean canTakeSelected = selectedSession != null && canTakeAttendance(selectedSession, now);
        String message = buildAttendanceMessage(selectedSession, canTakeSelected, now);
        return new AttendanceResponseDTO(
                lopHocPhanId,
                resolvedLichId,
                sessions,
                students,
                stats,
                canTakeSelected,
                message,
                now.toString());
    }

    private AttendanceResponseDTO.AttendanceStatsDTO buildStats(List<AttendanceStudentResponseDTO> students,
            int total) {
        int present = 0, absent = 0, late = 0, excused = 0, pending = 0;
        for (AttendanceStudentResponseDTO s : students) {
            if (s.getTrangThai() == null) {
                pending++;
            } else {
                switch (s.getTrangThai()) {
                    case "PRESENT":
                        present++;
                        break;
                    case "ABSENT":
                        absent++;
                        break;
                    case "LATE":
                        late++;
                        break;
                    case "EXCUSED":
                        excused++;
                        break;
                }
            }
        }
        int attended = present + late;
        double rate = total > 0 ? (double) attended / total * 100 : 0;
        return new AttendanceResponseDTO.AttendanceStatsDTO(total, present, absent, late, excused, pending, rate);
    }

    public void updateAttendance(UUID userId, AttendanceRequestDTO request) {
        validationService.validateLecturerAssignment(userId, request.getLopHocPhanId());
        LopHocPhan lopHocPhan = lopHocPhanRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần."));

        List<Lich> schedule = lopHocPhan.getDLichs();
        if (schedule.isEmpty())
            throw new RuntimeException("Lớp học phần chưa có lịch.");

        Lich lich = schedule.stream()
                .filter(l -> l.getId().toString().equals(request.getLichId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy buổi học."));

        if (!canTakeAttendance(lich, LocalDateTime.now())) {
            throw new RuntimeException("Chỉ được điểm danh trong đúng ngày và giờ học của buổi học này.");
        }

        for (AttendanceEntryDTO entry : request.getEntries()) {
            HocVien hocVien = dangKyTinChiRepository
                    .findByHocVien_IdAndLopHocPhan_Id(entry.getHocVienId(), request.getLopHocPhanId())
                    .map(DangKyTinChi::getHocVien)
                    .orElseThrow(() -> new RuntimeException("Sinh viên không thuộc lớp học phần."));

            DiemDanh diemDanh = attendanceRepository
                    .findByHocVien_IdAndLich_Id(entry.getHocVienId(), lich.getId())
                    .orElseGet(() -> {
                        DiemDanh dd = new DiemDanh();
                        dd.setHocVien(hocVien);
                        dd.setLich(lich);
                        return dd;
                    });

            diemDanh.setTrangThai(parseAttendanceStatus(entry.getTrangThai()));
            diemDanh.setGhiChu(entry.getGhiChu());
            diemDanh.setUpdatedAt(LocalDateTime.now());
            attendanceRepository.save(diemDanh);
        }
    }

    private boolean canTakeAttendance(Lich lich, LocalDateTime now) {
        if (lich == null || lich.getNgayHoc() == null || lich.getGioHoc() == null) {
            return false;
        }
        LocalDate scheduleDate = lich.getNgayHoc().toLocalDate();
        LocalTime start = lich.getGioHoc().getThoiGianBatDau();
        LocalTime end = lich.getGioHoc().getThoiGianKetThuc();
        LocalTime currentTime = now.toLocalTime();
        return now.toLocalDate().equals(scheduleDate)
                && (start == null || !currentTime.isBefore(start))
                && (end == null || !currentTime.isAfter(end));
    }

    private boolean isScheduleNotAfterNow(Lich lich, LocalDateTime now) {
        if (lich == null || lich.getNgayHoc() == null) {
            return false;
        }
        LocalDate date = lich.getNgayHoc().toLocalDate();
        LocalTime start = lich.getGioHoc() != null ? lich.getGioHoc().getThoiGianBatDau() : null;
        LocalDateTime sessionStart = LocalDateTime.of(date, start != null ? start : LocalTime.MIN);
        return !sessionStart.isAfter(now);
    }

    private String buildAttendanceMessage(Lich selectedSession, boolean canTakeSelected, LocalDateTime now) {
        if (selectedSession == null) {
            return "Chưa chọn buổi học để điểm danh.";
        }
        if (canTakeSelected) {
            return "Có thể điểm danh trong khung giờ học hiện tại.";
        }
        if (!now.toLocalDate().equals(selectedSession.getNgayHoc().toLocalDate())) {
            return "Buổi học không diễn ra trong ngày hiện tại nên chỉ được xem dữ liệu điểm danh.";
        }
        return "Ngoài khung giờ học quy định nên chỉ được xem dữ liệu điểm danh.";
    }

    private TrangThaiDiemDanhEnum parseAttendanceStatus(String status) {
        try {
            return TrangThaiDiemDanhEnum.valueOf(status);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new RuntimeException("Trạng thái điểm danh không hợp lệ.");
        }
    }
}
