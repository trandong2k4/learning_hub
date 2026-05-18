package com.university.service.lecturer;

import com.university.entity.DiemThanhPhan;
import com.university.entity.LopHocPhan;
import com.university.entity.Users;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.repository.lecturer.LecturerDiemThanhPhanRepository;
import com.university.repository.lecturer.LecturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LecturerValidationService {

    private final UsersAdminRepository userRepository;
    private final LecturerRepository lecturerRepository;
    private final LopHocPhanAdminRepository lopHocPhanRepository;
    private final LecturerDiemThanhPhanRepository diemThanhPhanRepository;

    public void validateLecturerAssignment(UUID userId, UUID lopHocPhanId) {
        if (lopHocPhanId == null) {
            lecturerRepository.findByUsers_Id(userId)
                    .orElseThrow(() -> new RuntimeException("Người dùng không phải giảng viên."));
            return;
        }
        boolean assigned = lopHocPhanRepository.findById(lopHocPhanId)
                .map(lhp -> lhp.getDGiangDays().stream()
                        .anyMatch(gd -> gd.getNhanVien().getUsers().getId().equals(userId)))
                .orElse(false);
        if (!assigned) {
            throw new RuntimeException("Giảng viên không được phân công cho lớp học phần này.");
        }
    }

    public Users loadActiveLecturerUser(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));
        lecturerRepository.findByUsers_Id(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không phải giảng viên."));
        if (!user.isTrangThai()) {
            throw new RuntimeException("Người dùng đang không hoạt động.");
        }
        return user;
    }

        public Float findAverageGrade(UUID lopHocPhanId, UUID hocVienId) {
                List<DiemThanhPhan> grades = diemThanhPhanRepository.findByDangKyTinChi_LopHocPhan_Id(lopHocPhanId).stream()
                                .filter(item -> item.getDangKyTinChi().getHocVien().getId().equals(hocVienId))
                                .collect(Collectors.toList());
                if (grades.isEmpty()) {
                        return null;
                }

                Map<UUID, DiemThanhPhan> latestByColumn = new LinkedHashMap<>();
                for (DiemThanhPhan grade : grades) {
                        if (grade.getCotDiem() != null) {
                                latestByColumn.putIfAbsent(grade.getCotDiem().getId(), grade);
                        }
                }

                double weightedSum = 0d;
                double totalWeight = 0d;
                boolean hasValidGrade = false;
                for (DiemThanhPhan grade : latestByColumn.values()) {
                        if (grade.getDiemSo() == null) {
                                continue;
                        }
                        double weight = parseWeight(grade.getCotDiem().getTiTrong());
                        if (weight <= 0d) {
                                continue;
                        }
                        weightedSum += grade.getDiemSo() * weight;
                        totalWeight += weight;
                        hasValidGrade = true;
                }

                if (!hasValidGrade || totalWeight == 0d) {
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

    public String firstPhong(LopHocPhan lopHocPhan) {
        return lopHocPhan.getDLichs().stream().findFirst().map(l -> l.getPhong().getTenPhong()).orElse(null);
    }

    public String firstToaNha(LopHocPhan lopHocPhan) {
        return lopHocPhan.getDLichs().stream().findFirst().map(l -> l.getPhong().getToaNha()).orElse(null);
    }
}
