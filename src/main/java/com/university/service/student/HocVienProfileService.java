package com.university.service.student;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.config.SecurityUtils;
import com.university.dto.request.student.HocVienProfileRequestDTO;
import com.university.dto.response.student.HocVienProfileResponseDTO;
import com.university.entity.HocVien;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.exception.students.HocVienChuaGanNganhException;
import com.university.repository.student.HocVienProfileRepository;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class HocVienProfileService {

    private final HocVienProfileRepository hocVienRepo;
    private final CurrentHocVienService currentHocVienService;

   
    @Transactional(readOnly = true)
    public HocVienProfileResponseDTO getProfile() {
        UUID userId = SecurityUtils.getCurrentUserId();
        HocVienProfileResponseDTO profile = hocVienRepo.findHocVienProfileByUserId(userId)
                .orElseThrow(() -> new SimpleMessageException("Khong tim thay hoc vien"));
        if (profile.getNganhId() == null) {
            throw new HocVienChuaGanNganhException("Hoc vien chua duoc gan nganh");
        }
        return profile;
    }

   
    public HocVienProfileResponseDTO updateProfile(HocVienProfileRequestDTO req) {
        HocVien hocVien = currentHocVienService.getCurrentHocVienForUpdateWithRelations();
        if (hocVien.getNganh() == null) {
            throw new HocVienChuaGanNganhException("Hoc vien chua duoc gan nganh");
        }
        Users user = hocVien.getUsers();
        user.setHoTen(req.getHoTen());
        user.setDiaChi(req.getDiaChi());
        user.setSoDienThoai(req.getSoDienThoai());
        user.setEmail(req.getEmail());
        user.setGioiTinh(req.getGioiTinh());
        user.setNgaySinh(req.getNgaySinh() != null ? req.getNgaySinh().atStartOfDay() : null);
        if (req.getCccd() != null) user.setCccd(req.getCccd());
        return new HocVienProfileResponseDTO(
                user.getId(),
                user.getUsername(), 
                user.getHoTen(),
                user.getDiaChi(),
                user.getSoDienThoai(),
                user.getEmail(),
                user.getGioiTinh(),
                user.getNgaySinh(),
                user.getCccd(),
                hocVien.getMaHocVien(),
                hocVien.getNganh().getId(),
                hocVien.getNgayNhapHoc(),
                hocVien.getNgayTotNghiep()
        );
    }
}
