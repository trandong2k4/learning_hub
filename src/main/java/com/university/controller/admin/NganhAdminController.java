package com.university.controller.admin;

import com.university.dto.request.admin.NganhAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.NganhAdminResponseDTO;
import com.university.service.admin.NganhAdminService;

import io.jsonwebtoken.io.IOException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/nganh")
@RequiredArgsConstructor
public class NganhAdminController {

    private final NganhAdminService nganhAdminService;

    @PostMapping
    public ResponseEntity<NganhAdminResponseDTO> create(@RequestBody @Valid NganhAdminRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nganhAdminService.create(dto));
    }

    @PostMapping("/import-excel")
    public ResponseEntity<ExcelImportResult> importExcel(@RequestParam("file") MultipartFile file)
            throws IOException, java.io.IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        ExcelImportResult result = nganhAdminService.importFromExcel(file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NganhAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(nganhAdminService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<NganhAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(nganhAdminService.getAllNganhResponseDTO());
    }

    @GetMapping("/search")
    public ResponseEntity<List<NganhAdminResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(nganhAdminService.search(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NganhAdminResponseDTO> update(@PathVariable UUID id,
            @RequestBody @Valid NganhAdminRequestDTO dto) {
        return ResponseEntity.ok(nganhAdminService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        nganhAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}