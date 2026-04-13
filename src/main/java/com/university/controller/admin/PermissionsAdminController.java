package com.university.controller.admin;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.university.dto.request.admin.PermissionsAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.PermissionsAdminResponseDTO;
import com.university.entity.Permissions;
import com.university.service.admin.PermissionsAdminService;

import io.jsonwebtoken.io.IOException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class PermissionsAdminController {

    private final PermissionsAdminService permissionsAdminService;

    @GetMapping
    public ResponseEntity<List<PermissionsAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(permissionsAdminService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permissions> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(permissionsAdminService.getPermissionsById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PermissionsAdminResponseDTO>> getByNamme(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(permissionsAdminService.getByMaPermissions(keyword));
    }

    @PostMapping
    public ResponseEntity<PermissionsAdminResponseDTO> create(@RequestBody PermissionsAdminRequestDTO dto) {
        return ResponseEntity.ok(permissionsAdminService.create(dto));
    }

    @PostMapping("/import-excel")
    public ResponseEntity<ExcelImportResult> importExcel(@RequestParam("file") MultipartFile file)
            throws IOException, java.io.IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        ExcelImportResult result = permissionsAdminService.importFromExcel(file);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionsAdminResponseDTO> update(@PathVariable UUID id,
            @RequestBody PermissionsAdminRequestDTO dto) {
        return ResponseEntity.ok(permissionsAdminService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        permissionsAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll() {
        permissionsAdminService.deleteAllPermissons();
        return ResponseEntity.noContent().build();
    }
}