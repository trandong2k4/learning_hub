package com.university.controller.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import com.university.dto.request.student.HocVienProfileRequestDTO;
import com.university.dto.response.student.HocVienProfileResponseDTO;
import com.university.service.student.HocVienProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.university.annotation.RequirePermission;

/**
 * Controller xử lý các API liên quan đến hồ sơ cá nhân của học viên.
 *
 * <p>Base URL: {@code /api/student/profile}</p>
 *
 * <p>Yêu cầu xác thực: học viên phải đăng nhập trước khi gọi các API này.
 * Thông tin học viên hiện tại được lấy từ Security Context.</p>
 */
@RestController
@RequestMapping("/api/student/profile")
@RequiredArgsConstructor
@RequirePermission("STU_PROFILE_VIEW")
public class HocVienProfileController {

    private final HocVienProfileService service;

    /**
     * Lấy thông tin hồ sơ của học viên đang đăng nhập.
     *
     * <p>GET {@code /api/student/profile}</p>
     *
     * @return {@link HocVienProfileResponseDTO} chứa toàn bộ thông tin cá nhân và học tập của học viên
     */
    @GetMapping
    public ResponseEntity<HocVienProfileResponseDTO> getProfile() {
        return ResponseEntity.ok(service.getProfile());
    }

    /**
     * Cập nhật thông tin hồ sơ cá nhân của học viên đang đăng nhập.
     *
     * <p>PUT {@code /api/student/profile}</p>
     *
     * <p>Chỉ cho phép cập nhật các trường thông tin cá nhân (họ tên, địa chỉ,
     * số điện thoại, email, giới tính, ngày sinh, CCCD).
     * Mã học viên và ngành học không thể thay đổi qua API này.</p>
     *
     * @param request dữ liệu cập nhật, được validate tự động (@Valid)
     * @return {@link HocVienProfileResponseDTO} phản ánh trạng thái sau khi cập nhật
     */
    @PutMapping
    public ResponseEntity<HocVienProfileResponseDTO> updateProfile(
            @Valid @RequestBody HocVienProfileRequestDTO request) {
        return ResponseEntity.ok(service.updateProfile(request));
    }
}
