package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.LopHocPhanAdminRequestDTO;
import com.university.dto.response.admin.LopHocPhanAdminResponseDTO;
import com.university.service.admin.LopHocPhanAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/lop-hoc-phan")
@RequiredArgsConstructor
@RequirePermission("ADMIN_LOP_HOC_PHAN_MANAGE_VIEW")
public class LopHocPhanAdminController {

    private final LopHocPhanAdminService lopHocPhanAdminService;

    @PostMapping
    public ResponseEntity<LopHocPhanAdminResponseDTO> create(
            @Valid @RequestBody LopHocPhanAdminRequestDTO request) {
        LopHocPhanAdminResponseDTO response = lopHocPhanAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LopHocPhanAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(lopHocPhanAdminService.getAll());
    }

    @GetMapping("/hoc-ki/{hocKiId}")
    public ResponseEntity<List<LopHocPhanAdminResponseDTO>> getAllByHocKi(@PathVariable UUID hocKiId) {
        return ResponseEntity.ok(lopHocPhanAdminService.getAllByHocKi(hocKiId));
    }

    @GetMapping("/mon-hoc/{monHocId}")
    public ResponseEntity<List<LopHocPhanAdminResponseDTO>> getAllByMonHoc(@PathVariable UUID monHocId) {
        return ResponseEntity.ok(lopHocPhanAdminService.getAllByMonHoc(monHocId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LopHocPhanAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(lopHocPhanAdminService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LopHocPhanAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody LopHocPhanAdminRequestDTO request) {
        return ResponseEntity.ok(lopHocPhanAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        lopHocPhanAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-list")
    public ResponseEntity<?> deleteList(@RequestBody List<UUID> ids) {
        List<String> cannotDelete = lopHocPhanAdminService.deleteAllByList(ids);
        if (cannotDelete.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(java.util.Map.of(
            "message", "Một số lớp học phần không thể xóa vì đã có sinh viên đăng ký",
            "cannotDelete", cannotDelete
        ));
    }
}
