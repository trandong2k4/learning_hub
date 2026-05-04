package com.university.controller.admin;

import com.university.dto.request.admin.BaiVietAdminRequestDTO;
import com.university.dto.request.admin.warrap.NhanVienCreateRequestDTO;
import com.university.dto.response.admin.BaiVietAdminResponseDTO;
import com.university.dto.response.admin.NhanVienAdminResponseDTO;
import com.university.dto.response.admin.warrap.NhanVienUsersResponseDTO;
import com.university.service.admin.BaiVietAdminService;
import com.university.service.admin.NhanVienAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/nhan-vien")
@RequiredArgsConstructor
public class NhanVienAdminController {
    private final NhanVienAdminService nhanVienAdminService;
    private final BaiVietAdminService baiVietService;

    @PostMapping
    public ResponseEntity<NhanVienUsersResponseDTO> create(
            @Valid @RequestBody NhanVienCreateRequestDTO request) {
        NhanVienUsersResponseDTO response = nhanVienAdminService.createDTO(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaiVietAdminResponseDTO> updateBaiViet(
            @PathVariable UUID id,
            @Valid @RequestBody BaiVietAdminRequestDTO request) {
        BaiVietAdminResponseDTO response = baiVietService.updateBaiViet(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaiVietAdminResponseDTO> getBaiViet(@PathVariable UUID id) {
        BaiVietAdminResponseDTO response = baiVietService.getBaiVietById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("all")
    public ResponseEntity<List<NhanVienAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(nhanVienAdminService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBaiViet(@PathVariable UUID id) {
        baiVietService.deleteBaiViet(id);
        return ResponseEntity.noContent().build();
    }
}
