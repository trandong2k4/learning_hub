package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.HocVienAdminRequestDTO;
import com.university.dto.request.admin.warrap.HocVienCreateRequestDTO;
import com.university.dto.request.admin.warrap.HocVienFullCreateRequestDTO;
import com.university.dto.response.admin.BatchDeleteResultDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.HocVienAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.service.admin.HocVienAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/hoc-vien")
@RequiredArgsConstructor
@RequirePermission("ADMIN_STUDENT_VIEW")
public class HocVienAdminController {

    private final HocVienAdminService hocVienAdminService;

    @PostMapping
    public ResponseEntity<HocVienAdminResponseDTO> create(
            @Valid @RequestBody HocVienCreateRequestDTO request) {
        HocVienAdminResponseDTO response = hocVienAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/full")
    public ResponseEntity<HocVienAdminResponseDTO> createFull(
            @Valid @RequestBody HocVienFullCreateRequestDTO request) {
        HocVienAdminResponseDTO response = hocVienAdminService.createFull(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<HocVienAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(hocVienAdminService.getAllHocVien());
    }

    @GetMapping("/available-users")
    public ResponseEntity<List<UsersAdminResponseDTO>> getAvailableUsers() {
        return ResponseEntity.ok(hocVienAdminService.getAvailableUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HocVienAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody HocVienAdminRequestDTO request) {
        HocVienAdminResponseDTO response = hocVienAdminService.updateHocVien(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/assign-user")
    public ResponseEntity<HocVienAdminResponseDTO> assignUser(
            @PathVariable UUID id,
            @RequestParam UUID usersId) {
        HocVienAdminResponseDTO response = hocVienAdminService.assignUser(id, usersId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HocVienAdminResponseDTO> getById(@PathVariable UUID id) {
        HocVienAdminResponseDTO response = hocVienAdminService.getHocVienById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        hocVienAdminService.deleteHocVien(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-list")
    public ResponseEntity<BatchDeleteResultDTO> deleteList(@RequestBody List<UUID> ids) {
        BatchDeleteResultDTO result = hocVienAdminService.deleteAllByList(ids);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/import-excel")
    public ResponseEntity<ExcelImportResult> importExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            ExcelImportResult err = new ExcelImportResult();
            err.setMessage("File không được để trống");
            return ResponseEntity.badRequest().body(err);
        }
        try {
            ExcelImportResult result = hocVienAdminService.importFromExcel(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ExcelImportResult err = new ExcelImportResult();
            err.setMessage("Lỗi khi import Excel: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}
