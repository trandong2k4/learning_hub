package com.university.service.lecturer;

import com.university.dto.request.lecturer.ChangePasswordRequestDTO;
import com.university.dto.request.lecturer.LecturerProfileRequestDTO;
import com.university.dto.response.lecturer.LecturerProfileResponseDTO;
import com.university.dto.response.lecturer.LecturerScheduleDTO;
import com.university.entity.FileStorage;
import com.university.entity.NhanVien;
import com.university.entity.Users;
import com.university.enums.FileEnum;
import com.university.mapper.lecturer.LecturerMapper;
import com.university.repository.admin.UsersAdminRepository;
import com.university.repository.lecturer.LecturerFileStorageRepository;
import com.university.repository.lecturer.LecturerRepository;
import com.university.repository.lecturer.LecturerScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LecturerProfileService {

    private final UsersAdminRepository userRepository;
    private final LecturerRepository lecturerRepository;
    private final LecturerScheduleRepository scheduleRepository;
    private final LecturerFileStorageRepository fileStorageRepository;
    private final LecturerMapper mapper;
    private final LecturerValidationService validationService;
    private final PasswordEncoder passwordEncoder;

    public LecturerProfileResponseDTO getProfile(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));
        NhanVien nhanVien = lecturerRepository.findByUsers_Id(userId).orElse(null);
        List<LecturerScheduleDTO> schedule = getSchedule(userId, LocalDate.now(), LocalDate.now().plusDays(7));
        return mapper.toProfileResponse(user, nhanVien, schedule);
    }

    public LecturerProfileResponseDTO updateProfile(UUID userId, LecturerProfileRequestDTO request) {
        Users user = validationService.loadActiveLecturerUser(userId);
        user.setSoDienThoai(request.getSoDienThoai());
        user.setEmail(request.getEmail());

        if (request.getHoTen() != null && !request.getHoTen().isBlank()) {
            user.setHoTen(request.getHoTen());
        }
        if (request.getDiaChi() != null) user.setDiaChi(request.getDiaChi());
        if (request.getGioiTinh() != null) user.setGioiTinh(request.getGioiTinh());
        if (request.getNgaySinh() != null) user.setNgaySinh(request.getNgaySinh());

        if (request.getAvatarUrl() != null) {
            FileStorage existing = fileStorageRepository.findByUsers_IdAndFileType(userId, FileEnum.AVATAR)
                    .stream().findFirst().orElse(null);
            if (request.getAvatarUrl().isBlank()) {
                // Remove from the managed collection so orphanRemoval deletes it on flush
                if (existing != null) user.getDFileStorages().remove(existing);
            } else if (existing == null) {
                // New avatar: add to collection so CascadeType.ALL inserts it on flush
                FileStorage avatar = new FileStorage();
                avatar.setUsers(user);
                avatar.setFileType(FileEnum.AVATAR);
                avatar.setFileName("avatar-");
                avatar.setFileUrl(request.getAvatarUrl());
                user.getDFileStorages().add(avatar);
            } else {
                // Update existing avatar in-place (same managed reference, mapper will see the new value)
                existing.setFileUrl(request.getAvatarUrl());
            }
        }

        userRepository.save(user);
        return getProfile(userId);
    }

    public void changePassword(UUID userId, ChangePasswordRequestDTO request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác.");
        }

        user.setPassWord(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public List<LecturerScheduleDTO> getSchedule(UUID userId, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.plusDays(1).atStartOfDay();
        return scheduleRepository
                .findByLopHocPhan_DGiangDays_NhanVien_Users_IdAndNgayHocBetween(userId, from, to)
                .stream().map(mapper::toScheduleDTO).collect(Collectors.toList());
    }
}
