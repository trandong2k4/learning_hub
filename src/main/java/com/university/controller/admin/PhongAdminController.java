package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.PhongAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.PhongAdminResponseDTO;
import com.university.service.admin.PhongAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/phong")
@RequiredArgsConstructor
@RequirePermission("ADMIN_SCHEDULE_MANAGE_VIEW")
public class PhongAdminController {

    private final PhongAdminService phongAdminService;

    @GetMapping
    public ResponseEntity<List<PhongAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(phongAdminService.getAllPhong());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhongAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(phongAdminService.getPhongById(id));
    }

    @PostMapping
    public ResponseEntity<PhongAdminResponseDTO> create(@Valid @RequestBody PhongAdminRequestDTO dto) {
        return ResponseEntity.ok(phongAdminService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhongAdminResponseDTO> update(@PathVariable UUID id,
            @Valid @RequestBody PhongAdminRequestDTO dto) {
        return ResponseEntity.ok(phongAdminService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        phongAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteBatch(@RequestParam List<UUID> ids) {
        phongAdminService.deleteAllByList(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import-excel")
    public ResponseEntity<ExcelImportResult> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(phongAdminService.importFromExcel(file));
    }
}
