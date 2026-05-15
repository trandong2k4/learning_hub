package com.university.service.student;

import com.university.dto.response.student.HocKiStudentsResponseDTO;
import com.university.entity.HocKi;
import com.university.repository.student.HocKiStudentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HocKiStudentsService {

    private final HocKiStudentsRepository hocKiStudentsRepository;

    public List<HocKiStudentsResponseDTO> getDanhSach() {
        LocalDateTime now = LocalDateTime.now();
        return hocKiStudentsRepository.findAllOrderByNgayBatDau()
                .stream()
                .map(hk -> toDTO(hk, now))
                .collect(Collectors.toList());
    }

    private HocKiStudentsResponseDTO toDTO(HocKi hk, LocalDateTime now) {
        boolean dangHoatDong = hk.getNgayBatDau() != null
                && hk.getNgayKetThuc() != null
                && !now.isBefore(hk.getNgayBatDau())
                && !now.isAfter(hk.getNgayKetThuc());

        // Năm học: HK1 bắt đầu tháng 9 → thuộc năm đó
        //          HK2 (tháng 2) và HKhè (tháng 6) → thuộc năm học trước (cùng nhóm với HK1)
        // Ngưỡng: tháng 9-12 → năm hiện tại; tháng 1-8 → năm trước
        int nam = 0;
        if (hk.getNgayBatDau() != null) {
            int month = hk.getNgayBatDau().getMonthValue();
            nam = (month >= 8) ? hk.getNgayBatDau().getYear() : hk.getNgayBatDau().getYear() - 1;
        }

        boolean laHocKiHe = hk.getNgayBatDau() != null
                && hk.getNgayBatDau().getMonthValue() >= 6
                && hk.getNgayBatDau().getMonthValue() <= 7;
        int maxTinChi = laHocKiHe ? 10 : 25;

        return new HocKiStudentsResponseDTO(
                hk.getId(),
                hk.getMaHocKi(),
                hk.getTenHocKi(),
                hk.getNgayBatDau(),
                hk.getNgayKetThuc(),
                dangHoatDong,
                nam,
                maxTinChi
        );
    }
}
