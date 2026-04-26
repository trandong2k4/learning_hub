package com.university.controller.admin;

import com.university.dto.response.admin.AdminStatsResponseDTO;
import com.university.dto.response.admin.WeeklyStatDTO;
import com.university.repository.admin.BaiVietAdminRepository;
import com.university.repository.admin.HocVienAdminRepository;
import com.university.repository.admin.KhoaAdminRepository;
import com.university.repository.admin.MonHocAdminRepository;
import com.university.repository.admin.NganhAdminRepository;
import com.university.repository.admin.NhanVienAdminRepository;
import com.university.repository.admin.TruongAdminRepository;
import com.university.repository.admin.UsersAdminRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final HocVienAdminRepository hocVienRepository;
    private final NganhAdminRepository nganhRepository;
    private final MonHocAdminRepository monHocRepository;
    private final BaiVietAdminRepository baiVietRepository;
    private final UsersAdminRepository userRepository;
    private final NhanVienAdminRepository nhanVienRepository;
    private final KhoaAdminRepository khoaRepository;
    private final TruongAdminRepository truongRepository;

    @GetMapping("/stats")
    public AdminStatsResponseDTO getStats() {
        long hocVienCount = hocVienRepository.count();
        long hocVienDangHoc = hocVienRepository.countByNgayTotNghiepIsNull();
        long hocVienTotNghiep = hocVienRepository.countByNgayTotNghiepIsNotNull();
        long nganhCount = nganhRepository.count();
        long khoaCount = khoaRepository.count();
        long truongCount = truongRepository.count();
        long monHocCount = monHocRepository.count();
        long baiVietCount = baiVietRepository.count();
        long userCount = userRepository.count();
        long giangVienCount = nhanVienRepository.countGiangVien();
        List<Object[]> rawNganh = hocVienRepository.countByNganhRaw();
        Map<String, Long> hocVienTheoNganh = new LinkedHashMap<>();
        for (Object[] row : rawNganh) {
            hocVienTheoNganh.put(
                    (String) row[0], // tên ngành
                    ((Number) row[1]).longValue() // số lượng
            );
        }

        List<Object[]> rawNam = hocVienRepository.countByNamNhapHocRaw();
        Map<Integer, Long> hocVienTheoNamNhapHoc = new LinkedHashMap<>();
        for (Object[] row : rawNam) {
            hocVienTheoNamNhapHoc.put(
                    ((Number) row[0]).intValue(), // năm nhập học
                    ((Number) row[1]).longValue() // số lượng
            );
        }

        return new AdminStatsResponseDTO(
                hocVienCount,
                hocVienDangHoc,
                hocVienTotNghiep,
                nganhCount,
                khoaCount,
                truongCount,
                monHocCount,
                baiVietCount,
                userCount,
                giangVienCount,
                hocVienTheoNganh,
                hocVienTheoNamNhapHoc);
    }

    @GetMapping("/stats/weekly")
    public WeeklyStatDTO getWeeklyStats() {
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            labels.add("Tuần " + i);
            values.add((long) hocVienRepository.countByNgayTotNghiepIsNull()
                    - hocVienRepository.countByNgayTotNghiepIsNotNull());
        }

        return new WeeklyStatDTO(labels, values);
    }
}
