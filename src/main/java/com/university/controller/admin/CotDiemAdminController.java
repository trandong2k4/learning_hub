package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.CotDiemAdminRequestDTO;
import com.university.dto.response.admin.CotDiemAdminResponseDTO;
import com.university.service.admin.CotDiemAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/cot-diem")
@RequiredArgsConstructor
@RequirePermission("ADMIN_LOP_HOC_PHAN_MANAGE_VIEW")
public class CotDiemAdminController {

    private final CotDiemAdminService cotDiemAdminService;

    @PostMapping
    public ResponseEntity<CotDiemAdminResponseDTO> create(
            @Valid @RequestBody CotDiemAdminRequestDTO request) {
        CotDiemAdminResponseDTO response = cotDiemAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CotDiemAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(cotDiemAdminService.getAll());
    }

    @GetMapping("/lop-hoc-phan/{lopHocPhanId}")
    public ResponseEntity<List<CotDiemAdminResponseDTO>> getAllByLopHocPhan(@PathVariable UUID lopHocPhanId) {
        return ResponseEntity.ok(cotDiemAdminService.getAllByLopHocPhan(lopHocPhanId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotDiemAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(cotDiemAdminService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CotDiemAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody CotDiemAdminRequestDTO request) {
        return ResponseEntity.ok(cotDiemAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cotDiemAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-list")
    public ResponseEntity<Void> deleteList(@RequestBody List<UUID> ids) {
        cotDiemAdminService.deleteAllByList(ids);
        return ResponseEntity.noContent().build();
    }
}
