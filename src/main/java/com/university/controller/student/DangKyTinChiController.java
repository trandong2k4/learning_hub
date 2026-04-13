package com.university.controller.student;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import com.university.dto.response.student.DangKyTinChiResponseDTO;
import com.university.service.student.DangKyTinChiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.university.dto.request.student.DangKyTinChiRequestDTO;

import java.util.UUID;

@RestController
@RequestMapping("/api/student/dang-ky-tin-chi")
@RequiredArgsConstructor
public class DangKyTinChiController {

    private final DangKyTinChiService dangKyTinChiService;

    // ✅ Đăng ký tín chỉ
    @PostMapping
    public ResponseEntity<DangKyTinChiResponseDTO> dangKy(
            @RequestBody @Valid DangKyTinChiRequestDTO request) {
        return ResponseEntity.ok(dangKyTinChiService.dangKy(request));
    }

    // ✅ Hủy đăng ký (dùng PathVariable)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> huyDangKyTinChi(@PathVariable UUID id) {
        dangKyTinChiService.huyDangKyTinChi(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Lấy theo học viên
    @GetMapping("/hoc-vien/{hocVienId}")
    public ResponseEntity<List<DangKyTinChiResponseDTO>> getByHocVienId(
            @PathVariable UUID hocVienId) {
        return ResponseEntity.ok(
                dangKyTinChiService.getDangKyTinChiByHocVienId(hocVienId)
        );
    }
}