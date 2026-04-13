package com.university.controller.admin;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.university.dto.request.admin.TruongAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.TruongAdminResponseDTO;
import com.university.dto.response.admin.TruongAdminResponseDTO.TruongView;
import com.university.service.admin.TruongAdminService;

import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/truong")
@RequiredArgsConstructor
public class TruongAdminController {

    private final TruongAdminService truongService;

    @GetMapping
    public ResponseEntity<List<TruongAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(truongService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TruongAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(truongService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TruongAdminResponseDTO>> getByNamme(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(truongService.getByName(keyword));
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<TruongView> getViewById(@PathVariable UUID id) {
        return ResponseEntity.ok(truongService.getViewById(id));
    }

    @PostMapping
    public ResponseEntity<TruongAdminResponseDTO> create(@Valid @RequestBody TruongAdminRequestDTO dto) {
        return ResponseEntity.ok(truongService.create(dto));
    }

    @PostMapping("/import-excel")
    public ResponseEntity<ExcelImportResult> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        ExcelImportResult result = truongService.importFromExcel(file);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TruongAdminResponseDTO> update(@PathVariable UUID id,
            @RequestBody TruongAdminRequestDTO dto) {
        return ResponseEntity.ok(truongService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        truongService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteList(@RequestParam List<UUID> ids) {
        truongService.deleteMultiple(ids);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll() {
        truongService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}