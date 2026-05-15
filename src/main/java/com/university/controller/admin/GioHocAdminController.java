package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.GioHocAdminRequestDTO;
import com.university.dto.response.admin.GioHocAdminResponseDTO;
import com.university.service.admin.GioHocAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/gio-hoc")
@RequiredArgsConstructor
@RequirePermission("ADMIN_SCHEDULE_MANAGE_VIEW")
public class GioHocAdminController {

    private final GioHocAdminService gioHocAdminService;

    @GetMapping
    public ResponseEntity<List<GioHocAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(gioHocAdminService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GioHocAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(gioHocAdminService.getById(id));
    }

    @GetMapping("/search-name")
    public ResponseEntity<List<GioHocAdminResponseDTO>> searchByName(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(gioHocAdminService.getByTenGioHoc(keyword));
    }

    @PostMapping
    public ResponseEntity<GioHocAdminResponseDTO> create(@Valid @RequestBody GioHocAdminRequestDTO dto) {
        return ResponseEntity.ok(gioHocAdminService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GioHocAdminResponseDTO> update(@PathVariable UUID id,
            @Valid @RequestBody GioHocAdminRequestDTO dto) {
        return ResponseEntity.ok(gioHocAdminService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        gioHocAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-list")
    public ResponseEntity<Void> deleteList(@RequestParam List<UUID> ids) {
        gioHocAdminService.deleteAllByList(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import-excel")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(gioHocAdminService.importFromExcel(file));
    }
}