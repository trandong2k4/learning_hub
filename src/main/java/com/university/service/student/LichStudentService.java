package com.university.service.student;

import com.university.config.SecurityUtils;
import com.university.dto.response.student.LichCaNhanStudentItemResponse;
import com.university.dto.response.student.LichCaNhanStudentResponse;
import com.university.entity.GioHoc;
import com.university.entity.Lich;
import com.university.repository.student.LichStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LichStudentService {

    private static final String VIEW_DAY = "DAY";
    private static final String VIEW_WEEK = "WEEK";
    private static final String VIEW_MONTH = "MONTH";

    private final LichStudentRepository lichStudentRepository;

    public LichCaNhanStudentResponse getLichCaNhan(String view, LocalDate date) {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        String normalizedView = normalizeView(view);
        LocalDate referenceDate = date != null ? date : LocalDate.now();
        DateRange range = resolveDateRange(normalizedView, referenceDate);

        List<LichCaNhanStudentItemResponse> scheduleItems = lichStudentRepository
                .findPersonalSchedule(hocVienId, range.start().atStartOfDay(), range.endExclusive().atStartOfDay())
                .stream()
                .map(this::toResponseItem)
                .toList();

        return LichCaNhanStudentResponse.builder()
                .cheDoXem(normalizedView)
                .ngayThamChieu(referenceDate)
                .tuNgay(range.start())
                .denNgay(range.endExclusive().minusDays(1))
                .tongSuKien(scheduleItems.size())
                .lich(scheduleItems)
                .build();
    }

    private String normalizeView(String rawView) {
        String normalizedView = rawView == null ? VIEW_WEEK : rawView.trim().toUpperCase(Locale.ROOT);
        if (!VIEW_DAY.equals(normalizedView) && !VIEW_WEEK.equals(normalizedView) && !VIEW_MONTH.equals(normalizedView)) {
            throw new IllegalArgumentException("Che do xem khong hop le. Ho tro: DAY, WEEK, MONTH");
        }
        return normalizedView;
    }

    private DateRange resolveDateRange(String view, LocalDate referenceDate) {
        return switch (view) {
            case VIEW_DAY -> new DateRange(referenceDate, referenceDate.plusDays(1));
            case VIEW_WEEK -> {
                LocalDate startOfWeek = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                yield new DateRange(startOfWeek, startOfWeek.plusDays(7));
            }
            case VIEW_MONTH -> {
                LocalDate startOfMonth = referenceDate.withDayOfMonth(1);
                yield new DateRange(startOfMonth, startOfMonth.plusMonths(1));
            }
            default -> throw new IllegalArgumentException("Che do xem khong hop le");
        };
    }

    private LichCaNhanStudentItemResponse toResponseItem(Lich lich) {
        validateScheduleData(lich);

        GioHoc gioHoc = lich.getGioHoc();
        return LichCaNhanStudentItemResponse.builder()
                .lichId(lich.getId())
                .lopHocPhanId(lich.getLopHocPhan().getId())
                .maLopHocPhan(lich.getLopHocPhan().getMaLopHocPhan())
                .monHocId(lich.getLopHocPhan().getMonHoc().getId())
                .maMonHoc(lich.getLopHocPhan().getMonHoc().getMaMonHoc())
                .tenMonHoc(lich.getLopHocPhan().getMonHoc().getTenMonHoc())
                .ngayHoc(lich.getNgayHoc())
                .thoiGianBatDau(gioHoc.getThoiGianBatDau())
                .thoiGianKetThuc(gioHoc.getThoiGianKetThuc())
                .phongId(lich.getPhong().getId())
                .maPhong(lich.getPhong().getMaPhong())
                .tenPhong(lich.getPhong().getTenPhong())
                .ghiChu(lich.getGhiChu())
                .build();
    }

    private void validateScheduleData(Lich lich) {
        if (lich.getNgayHoc() == null) {
            throw new IllegalStateException("Du lieu lich khong hop le: thieu ngay hoc");
        }
        if (lich.getGioHoc() == null
                || lich.getGioHoc().getThoiGianBatDau() == null
                || lich.getGioHoc().getThoiGianKetThuc() == null) {
            throw new IllegalStateException("Du lieu lich khong hop le: thieu gio hoc");
        }
        if (!lich.getGioHoc().getThoiGianBatDau().isBefore(lich.getGioHoc().getThoiGianKetThuc())) {
            throw new IllegalStateException("Du lieu lich khong hop le: khoang thoi gian khong dung");
        }
        if (lich.getPhong() == null || lich.getLopHocPhan() == null || lich.getLopHocPhan().getMonHoc() == null) {
            throw new IllegalStateException("Du lieu lich khong hop le: thieu thong tin mon hoc hoac phong hoc");
        }
    }

    private record DateRange(LocalDate start, LocalDate endExclusive) {
    }
}
