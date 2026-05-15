package com.university.service.student;

import com.university.dto.response.student.LichStudentsResponseDTO;
import com.university.repository.student.LichStudentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LichStudentsService {

    private final LichStudentsRepository lichStudentsRepository;

    public List<LichStudentsResponseDTO> getLichByLopHocPhan(UUID lopHocPhanId) {
        return lichStudentsRepository.findByLopHocPhanId(lopHocPhanId).stream()
                .sorted(Comparator.comparing(l -> l.getNgayHoc()))
                .map(l -> new LichStudentsResponseDTO(
                        toThu(l.getNgayHoc().getDayOfWeek()),
                        l.getNgayHoc().toLocalDate(),
                        l.getGioHoc().getThoiGianBatDau(),
                        l.getGioHoc().getThoiGianKetThuc(),
                        l.getGioHoc().getTenGioHoc(),
                        l.getPhong().getMaPhong(),
                        l.getPhong().getTenPhong()
                ))
                .collect(Collectors.toList());
    }

    private String toThu(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> "Thứ 2";
            case TUESDAY -> "Thứ 3";
            case WEDNESDAY -> "Thứ 4";
            case THURSDAY -> "Thứ 5";
            case FRIDAY -> "Thứ 6";
            case SATURDAY -> "Thứ 7";
            case SUNDAY -> "Chủ nhật";
        };
    }
}
