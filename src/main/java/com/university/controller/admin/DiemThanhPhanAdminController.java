package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.DiemThanhPhanAdminRequestDTO;
import com.university.dto.response.admin.DiemThanhPhanAdminResponseDTO;
import com.university.service.admin.DiemThanhPhanAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/diem-thanh-phan")
@RequiredArgsConstructor
@RequirePermission("ADMIN_LOP_HOC_PHAN_MANAGE_VIEW")
public class DiemThanhPhanAdminController {

    private final DiemThanhPhanAdminService diemService;

    @PostMapping
    public ResponseEntity<DiemThanhPhanAdminResponseDTO> create(
            @Valid @RequestBody DiemThanhPhanAdminRequestDTO request) {
        DiemThanhPhanAdminResponseDTO response = diemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiemThanhPhanAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody DiemThanhPhanAdminRequestDTO request) {
        DiemThanhPhanAdminResponseDTO response = diemService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiemThanhPhanAdminResponseDTO> getById(@PathVariable UUID id) {
        DiemThanhPhanAdminResponseDTO response = diemService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DiemThanhPhanAdminResponseDTO.DiemThanhPhanView>> getAll() {
        return ResponseEntity.ok(diemService.getAllView());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        diemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/by-list")
    public ResponseEntity<String> deleteList(@RequestBody List<UUID> ids) {
        diemService.deleteAllByList(ids);
        return ResponseEntity.ok("Xóa thành công " + ids.size() + " điểm thành phần");
    }
}
