package com.university.service.accounting;

import com.university.config.SecurityUtils;
import com.university.dto.request.accounting.AccountingChangePasswordRequestDTO;
import com.university.dto.request.accounting.AccountingProfileRequestDTO;
import com.university.dto.response.accounting.AccountingProfileResponseDTO;
import com.university.entity.NhanVien;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.repository.student.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountingProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AccountingProfileResponseDTO getProfile() {
        System.out.println("Fetching profile for user");
        Users user = getCurrentUser();
        System.out.println("User: " + user.getUsername() + ", ID: " + user.getId());
        return toResponse(user);
    }

    public AccountingProfileResponseDTO updateProfile(AccountingProfileRequestDTO req) {
        Users user = getCurrentUser();
        user.setHoTen(req.getHoTen());
        user.setDiaChi(req.getDiaChi());
        user.setSoDienThoai(req.getSoDienThoai());
        user.setGioiTinh(req.getGioiTinh());
        user.setNgaySinh(req.getNgaySinh() != null ? req.getNgaySinh().atStartOfDay() : null);
        return toResponse(user);
    }

    public void changePassword(AccountingChangePasswordRequestDTO req) {
        Users user = getCurrentUser();
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }
        user.setPassWord(passwordEncoder.encode(req.getNewPassword()));
    }

    private Users getCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new SimpleMessageException("Không tìm thấy người dùng"));
    }

    private AccountingProfileResponseDTO toResponse(Users user) {
        NhanVien nv = user.getNhanVien();
        return AccountingProfileResponseDTO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .hoTen(user.getHoTen())
                .email(user.getEmail())
                .soDienThoai(user.getSoDienThoai())
                .diaChi(user.getDiaChi())
                .gioiTinh(user.getGioiTinh())
                .ngaySinh(user.getNgaySinh())
                .maNhanVien(nv != null ? nv.getMaNhanVien() : null)
                .ngayNhanViec(nv != null ? nv.getNgayNhanViec() : null)
                .build();
    }
}
