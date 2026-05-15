package com.university.service.lecturer;

import com.university.dto.response.lecturer.LecturerDashboardResponseDTO;
import com.university.entity.DiemDanh;
import com.university.entity.Lich;
import com.university.entity.LopHocPhan;
import com.university.entity.QuizAttempt;
import com.university.entity.SubmitExercise;
import com.university.entity.ThongBao;
import com.university.entity.ThongBaoNguoiDung;
import com.university.enums.TrangThaiDiemDanhEnum;
import com.university.mapper.lecturer.LecturerMapper;
import com.university.repository.lecturer.LecturerAttendanceRepository;
import com.university.repository.lecturer.LecturerDangKyTinChiRepository;
import com.university.repository.lecturer.LecturerNotificationRepository;
import com.university.repository.lecturer.LecturerQuizAttemptRepository;
import com.university.repository.lecturer.LecturerScheduleRepository;
import com.university.repository.lecturer.LecturerSubmitExerciseRepository;
import com.university.repository.lecturer.LecturerThongBaoNguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LecturerDashboardService {

    private final LecturerScheduleRepository scheduleRepository;
    private final LecturerAttendanceRepository attendanceRepository;
    private final LecturerSubmitExerciseRepository submitExerciseRepository;
    private final LecturerQuizAttemptRepository quizAttemptRepository;
    private final LecturerDangKyTinChiRepository dangKyTinChiRepository;
    private final LecturerNotificationRepository notificationRepository;
    private final LecturerThongBaoNguoiDungRepository thongBaoNguoiDungRepository;
    private final LecturerMapper mapper;
    private final LecturerValidationService validationService;

    public LecturerDashboardResponseDTO getDashboard(UUID userId) {
        validationService.validateLecturerAssignment(userId, null);
        LocalDate today = LocalDate.now();

        LocalDateTime todayStart = today.atStartOfDay();
        var todaySchedule = scheduleRepository
                .findByLopHocPhan_DGiangDays_NhanVien_Users_IdAndNgayHocBetween(userId, todayStart, todayStart.plusDays(1))
                .stream().map(mapper::toScheduleDTO).collect(Collectors.toList());

        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);
        var weekSchedule = scheduleRepository
                .findByLopHocPhan_DGiangDays_NhanVien_Users_IdAndNgayHocBetween(userId,
                        weekStart.atStartOfDay(), weekEnd.plusDays(1).atStartOfDay())
                .stream().map(mapper::toScheduleDTO).collect(Collectors.toList());

        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        var monthSchedule = scheduleRepository
                .findByLopHocPhan_DGiangDays_NhanVien_Users_IdAndNgayHocBetween(userId,
                        monthStart.atStartOfDay(), monthEnd.plusDays(1).atStartOfDay())
                .stream().map(mapper::toScheduleDTO).collect(Collectors.toList());

        List<Lich> allSchedules = scheduleRepository.findByLopHocPhan_DGiangDays_NhanVien_Users_Id(userId);
        Map<UUID, List<Lich>> schedulesByClass = allSchedules.stream()
                .collect(Collectors.groupingBy(lich -> lich.getLopHocPhan().getId(), LinkedHashMap::new, Collectors.toList()));

        List<LecturerDashboardResponseDTO.DashboardClassDTO> teachingClasses = schedulesByClass.values().stream()
                .map(classSchedules -> {
                    Lich first = classSchedules.get(0);
                    LopHocPhan lopHocPhan = first.getLopHocPhan();
                    LocalDateTime start = classSchedules.stream()
                            .map(Lich::getNgayHoc)
                            .min(LocalDateTime::compareTo)
                            .orElse(null);
                    LocalDateTime end = classSchedules.stream()
                            .map(Lich::getNgayHoc)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);
                    return new LecturerDashboardResponseDTO.DashboardClassDTO(
                            lopHocPhan.getId(),
                            lopHocPhan.getMaLopHocPhan(),
                            lopHocPhan.getMonHoc().getTenMonHoc(),
                            validationService.firstPhong(lopHocPhan),
                            validationService.firstToaNha(lopHocPhan),
                            start,
                            end,
                            (int) dangKyTinChiRepository.countByLopHocPhan_Id(lopHocPhan.getId()),
                            classSchedules.size()
                    );
                })
                .sorted(Comparator.comparing(LecturerDashboardResponseDTO.DashboardClassDTO::getMaLopHocPhan,
                        Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());

        int totalClasses = teachingClasses.size();
        int totalStudents = teachingClasses.stream()
                .mapToInt(LecturerDashboardResponseDTO.DashboardClassDTO::getStudentCount)
                .sum();

        int ungradedAssignments = submitExerciseRepository.countUngradedSubmissionsByLecturer(userId);
        int pendingQuizAttempts = quizAttemptRepository.countCompletedAttemptsByLecturer(userId);
        int totalPendingWork = ungradedAssignments + pendingQuizAttempts;

        List<DiemDanh> allAttendance = attendanceRepository.findByLich_LopHocPhan_DGiangDays_NhanVien_Users_Id(userId);
        double attendanceRate = 0.0;
        if (!allAttendance.isEmpty()) {
            long presentCount = allAttendance.stream()
                    .filter(d -> TrangThaiDiemDanhEnum.PRESENT.equals(d.getTrangThai())
                            || TrangThaiDiemDanhEnum.LATE.equals(d.getTrangThai()))
                    .count();
            attendanceRate = presentCount / (double) allAttendance.size();
        }

        List<LecturerDashboardResponseDTO.DashboardNotificationDTO> recentNotifications =
                buildRecentNotifications(userId);
        List<LecturerDashboardResponseDTO.DashboardWorkItemDTO> pendingWorkItems =
                buildPendingWorkItems(userId);

        return new LecturerDashboardResponseDTO(
                todaySchedule,
                weekSchedule,
                monthSchedule,
                totalClasses,
                ungradedAssignments,
                attendanceRate,
                totalStudents,
                pendingQuizAttempts,
                totalPendingWork,
                teachingClasses,
                recentNotifications,
                pendingWorkItems);
    }

    private List<LecturerDashboardResponseDTO.DashboardNotificationDTO> buildRecentNotifications(UUID userId) {
        List<LecturerDashboardResponseDTO.DashboardNotificationDTO> notifications = new ArrayList<>();

        List<ThongBaoNguoiDung> receivedNotifications =
                thongBaoNguoiDungRepository.findRecentByUserId(userId, PageRequest.of(0, 5));
        receivedNotifications.forEach(item -> {
            ThongBao thongBao = item.getThongBao();
            notifications.add(new LecturerDashboardResponseDTO.DashboardNotificationDTO(
                    thongBao.getId(),
                    thongBao.getTieuDe(),
                    thongBao.getNoiDung(),
                    thongBao.getLoaiThongBao() != null ? thongBao.getLoaiThongBao().name() : null,
                    thongBao.getCreatedAt(),
                    item.getDaNhan(),
                    "RECEIVED"));
        });

        if (notifications.size() < 5) {
            List<ThongBao> sentNotifications = notificationRepository.findByUsers_IdOrderByCreatedAtDesc(userId);
            sentNotifications.stream()
                    .limit(5L - notifications.size())
                    .forEach(thongBao -> notifications.add(new LecturerDashboardResponseDTO.DashboardNotificationDTO(
                            thongBao.getId(),
                            thongBao.getTieuDe(),
                            thongBao.getNoiDung(),
                            thongBao.getLoaiThongBao() != null ? thongBao.getLoaiThongBao().name() : null,
                            thongBao.getCreatedAt(),
                            true,
                            "SENT")));
        }

        return notifications.stream()
                .sorted(Comparator.comparing(LecturerDashboardResponseDTO.DashboardNotificationDTO::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<LecturerDashboardResponseDTO.DashboardWorkItemDTO> buildPendingWorkItems(UUID userId) {
        List<LecturerDashboardResponseDTO.DashboardWorkItemDTO> workItems = new ArrayList<>();

        List<SubmitExercise> submissions =
                submitExerciseRepository.findUngradedSubmissionsByLecturer(userId, PageRequest.of(0, 5));
        submissions.forEach(submission -> {
            var exercise = submission.getExercise();
            var lopHocPhan = exercise.getLopHocPhan();
            workItems.add(new LecturerDashboardResponseDTO.DashboardWorkItemDTO(
                    String.valueOf(submission.getPhienThucHien()),
                    "ASSIGNMENT",
                    exercise.getTieuDe(),
                    lopHocPhan.getMaLopHocPhan(),
                    lopHocPhan.getMonHoc().getTenMonHoc(),
                    submission.getHocVien().getUsers().getHoTen(),
                    submission.getThoiGianNop() != null ? submission.getThoiGianNop() : submission.getCreatedAt(),
                    "/lecturer/assignments/" + exercise.getId() + "/submissions?classId=" + lopHocPhan.getId()
            ));
        });

        List<QuizAttempt> quizAttempts =
                quizAttemptRepository.findCompletedAttemptsByLecturer(userId, PageRequest.of(0, 5));
        quizAttempts.forEach(attempt -> {
            var quiz = attempt.getQuiz();
            var lopHocPhan = quiz.getLopHocPhan();
            workItems.add(new LecturerDashboardResponseDTO.DashboardWorkItemDTO(
                    attempt.getId().toString(),
                    "QUIZ",
                    quiz.getTieuDe(),
                    lopHocPhan.getMaLopHocPhan(),
                    lopHocPhan.getMonHoc().getTenMonHoc(),
                    attempt.getHocVien().getUsers().getHoTen(),
                    attempt.getEndTime() != null ? attempt.getEndTime() : attempt.getStartTime(),
                    "/lecturer/quiz/" + quiz.getId() + "/results?classId=" + lopHocPhan.getId()
            ));
        });

        return workItems.stream()
                .sorted(Comparator.comparing(LecturerDashboardResponseDTO.DashboardWorkItemDTO::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(6)
                .collect(Collectors.toList());
    }
}
