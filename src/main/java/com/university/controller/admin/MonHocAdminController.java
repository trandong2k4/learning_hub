package com.university.controller.admin;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.university.dto.request.admin.MonHocAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.MonHocAdminResponseDTO;
import com.university.service.admin.MonHocAdminService;

import io.jsonwebtoken.io.IOException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/mon-hoc")
@RequiredArgsConstructor
public class MonHocAdminController {

    private final MonHocAdminService monHocAdminService;

    @PostMapping
    public ResponseEntity<MonHocAdminResponseDTO> create(@RequestBody MonHocAdminRequestDTO dto) {
        return ResponseEntity.ok(monHocAdminService.create(dto));
    }

    @PostMapping("/import-excel")
    public ResponseEntity<ExcelImportResult> importExcel(@RequestParam("file") MultipartFile file)
            throws IOException, java.io.IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        ExcelImportResult result = monHocAdminService.importFromExcel(file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MonHocAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(monHocAdminService.getALLMonHOCDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonHocAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(monHocAdminService.getMonHocById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MonHocAdminResponseDTO>> getByNamme(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(monHocAdminService.getMonHocByName(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonHocAdminResponseDTO> update(@PathVariable UUID id,
            @RequestBody MonHocAdminRequestDTO dto) {
        return ResponseEntity.ok(monHocAdminService.updateMonHoc(id, dto));
    }

    @DeleteMapping("/delete-list")
    public ResponseEntity<Void> delete(@RequestBody List<UUID> ids) {
        monHocAdminService.deleteAllByList(ids);
        return ResponseEntity.noContent().build();
    }
}