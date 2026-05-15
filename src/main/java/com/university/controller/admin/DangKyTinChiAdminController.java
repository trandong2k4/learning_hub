package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.DangKyTinChiAdminRequestDTO;
import com.university.dto.response.admin.DangKyTinChiAdminResponseDTO;
import com.university.service.admin.DangKyTinChiAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/dang-ky-tin-chi")
@RequiredArgsConstructor
public class DangKyTinChiAdminController {

    private final DangKyTinChiAdminService dangKyTinChiAdminService;

    @PostMapping
    @RequirePermission("ADMIN_ENROLLMENT_CREATE")
    public ResponseEntity<DangKyTinChiAdminResponseDTO> create(
            @Valid @RequestBody DangKyTinChiAdminRequestDTO request) {
        DangKyTinChiAdminResponseDTO response = dangKyTinChiAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @RequirePermission("ADMIN_ENROLLMENT_VIEW")
    public ResponseEntity<List<DangKyTinChiAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(dangKyTinChiAdminService.getAll());
    }

    @GetMapping("/chi-tiet")
    @RequirePermission("ADMIN_ENROLLMENT_VIEW")
    public ResponseEntity<List<DangKyTinChiAdminResponseDTO>> getAllDetail() {
        return ResponseEntity.ok(dangKyTinChiAdminService.getAllDetail());
    }

    @GetMapping("/{id}")
    @RequirePermission("ADMIN_ENROLLMENT_VIEW")
    public ResponseEntity<DangKyTinChiAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(dangKyTinChiAdminService.getById(id));
    }

    @GetMapping("/hoc-ki/{hocKiId}")
    @RequirePermission("ADMIN_ENROLLMENT_VIEW")
    public ResponseEntity<List<DangKyTinChiAdminResponseDTO>> getByHocKi(@PathVariable UUID hocKiId) {
        return ResponseEntity.ok(dangKyTinChiAdminService.getAllByHocKi(hocKiId));
    }

    @GetMapping("/hoc-vien/{hocVienId}")
    @RequirePermission("ADMIN_ENROLLMENT_VIEW")
    public ResponseEntity<List<DangKyTinChiAdminResponseDTO>> getByHocVien(@PathVariable UUID hocVienId) {
        return ResponseEntity.ok(dangKyTinChiAdminService.getAllByHocVien(hocVienId));
    }

    @GetMapping("/lop-hoc-phan/{lopHocPhanId}")
    @RequirePermission("ADMIN_ENROLLMENT_VIEW")
    public ResponseEntity<List<DangKyTinChiAdminResponseDTO>> getByLopHocPhan(@PathVariable UUID lopHocPhanId) {
        return ResponseEntity.ok(dangKyTinChiAdminService.getAllByLopHocPhan(lopHocPhanId));
    }

    @GetMapping("/tim-kiem")
    @RequirePermission("ADMIN_ENROLLMENT_VIEW")
    public ResponseEntity<List<DangKyTinChiAdminResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(dangKyTinChiAdminService.search(keyword));
    }

    @GetMapping("/dropdown/lop-hoc-phan-mo-dang-ky")
    @RequirePermission("ADMIN_ENROLLMENT_VIEW")
    public ResponseEntity<List<DangKyTinChiAdminResponseDTO.LopHocPhanDangKyView>> getLopHocPhanMoDangKy() {
        return ResponseEntity.ok(dangKyTinChiAdminService.getLopHocPhanMoDangKy());
    }

    @GetMapping("/dropdown/hoc-vien")
    @RequirePermission("ADMIN_ENROLLMENT_VIEW")
    public ResponseEntity<List<DangKyTinChiAdminResponseDTO.HocVienDangKyView>> getHocVienDangKy() {
        return ResponseEntity.ok(dangKyTinChiAdminService.getHocVienDangKy());
    }

    @PutMapping("/{id}")
    @RequirePermission("ADMIN_ENROLLMENT_CREATE")
    public ResponseEntity<DangKyTinChiAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody DangKyTinChiAdminRequestDTO request) {
        return ResponseEntity.ok(dangKyTinChiAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RequirePermission("ADMIN_ENROLLMENT_CANCEL")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        dangKyTinChiAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/xoa-nhieu")
    @RequirePermission("ADMIN_ENROLLMENT_DELETE")
    public ResponseEntity<Void> deleteList(@RequestBody List<UUID> ids) {
        dangKyTinChiAdminService.deleteAllByList(ids);
        return ResponseEntity.noContent().build();
    }
}
