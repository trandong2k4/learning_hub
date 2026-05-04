package com.university.service.student;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.university.config.SecurityUtils;
import com.university.dto.request.student.HocVienProfileRequestDTO;
import com.university.dto.response.student.HocVienProfileResponseDTO;
import com.university.entity.HocVien;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.repository.student.HocVienProfileRepository;
import com.university.repository.student.UserRepository;

@Service
public class HocVienProfileService {

    private final HocVienProfileRepository hocVienRepo;
    private final UserRepository userRepo;

    public HocVienProfileService(HocVienProfileRepository hocVienRepo, UserRepository userRepo) {
        this.hocVienRepo = hocVienRepo;
        this.userRepo = userRepo;
    }

    public HocVienProfileResponseDTO getProfile() {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        return hocVienRepo.findHocVienProfileByHocVienId(hocVienId)
                .orElseThrow(() -> new SimpleMessageException("Khong tim thay hoc vien"));
    }

    public HocVienProfileResponseDTO updateProfile(HocVienProfileRequestDTO req) {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();

        HocVien hocVien = hocVienRepo.findById(hocVienId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay hoc vien"));

        Users user = userRepo.findById(hocVien.getUsers().getId())
                .orElseThrow(() -> new IllegalArgumentException("User khong ton tai"));

        user.setHoTen(req.getHoTen());
        user.setDiaChi(req.getDiaChi());
        user.setSoDienThoai(req.getSoDienThoai());
        user.setEmail(req.getEmail());
        user.setGioiTinh(req.getGioiTinh());
        user.setNgaySinh(req.getNgaySinh());
        user.setCccd(req.getCccd());

        userRepo.save(user);
        return getProfile();
    }
}
