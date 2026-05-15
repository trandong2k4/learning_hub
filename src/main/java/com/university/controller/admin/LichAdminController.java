package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.GioHocAdminRequestDTO;
import com.university.dto.request.admin.LichAdminRequestDTO;
import com.university.dto.request.admin.PhongAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.GioHocAdminResponseDTO;
import com.university.dto.response.admin.LichAdminResponseDTO;
import com.university.dto.response.admin.PhongAdminResponseDTO;
import com.university.service.admin.LichAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/lich")
@RequiredArgsConstructor
@RequirePermission("ADMIN_SCHEDULE_MANAGE_VIEW")
public class LichAdminController {

    private final LichAdminService lichService;

    // ==================== LICH HOC ====================

    @PostMapping
    public ResponseEntity<LichAdminResponseDTO> createLich(
            @Valid @RequestBody LichAdminRequestDTO request) {
        LichAdminResponseDTO response = lichService.createLich(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LichAdminResponseDTO> updateLich(
            @PathVariable UUID id,
            @Valid @RequestBody LichAdminRequestDTO request) {
        LichAdminResponseDTO response = lichService.updateLich(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LichAdminResponseDTO> getLich(@PathVariable UUID id) {
        LichAdminResponseDTO response = lichService.getLichById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LichAdminResponseDTO>> getAllLich() {
        return ResponseEntity.ok(lichService.getAllLich());
    }

    @GetMapping("/lop-hoc-phan/{id}")
    public ResponseEntity<List<LichAdminResponseDTO>> getAllLichByLopHocPhanId(@PathVariable UUID id) {
        return ResponseEntity.ok(lichService.getAllLichByLopHopPhan(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLich(@PathVariable UUID id) {
        lichService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/by-list")
    public ResponseEntity<?> deleteList(@RequestBody List<UUID> ids) {
        List<String> errors = lichService.deleteAllByList(ids);
        if (errors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(java.util.Map.of(
            "message", "Một số lịch không thể xóa",
            "errors", errors
        ));
    }

    // ==================== PHONG HOC ====================

    @PostMapping("/phong")
    public ResponseEntity<PhongAdminResponseDTO> createPhong(@Valid @RequestBody PhongAdminRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lichService.createPhong(request));
    }

    @PostMapping("/phong/list")
    public ResponseEntity<String> createPhongList(@Valid @RequestBody List<PhongAdminRequestDTO> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lichService.createPhongList(request));
    }

    @PostMapping("/phong/import-excel")
    public ResponseEntity<ExcelImportResult> importPhongExcel(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(lichService.importPhongFromExcel(file));
    }

    @GetMapping("/phong")
    public ResponseEntity<List<PhongAdminResponseDTO>> getAllPhong() {
        return ResponseEntity.ok(lichService.getAllPhong());
    }

    @GetMapping("/phong/{id}")
    public ResponseEntity<PhongAdminResponseDTO> getPhongById(@PathVariable UUID id) {
        return ResponseEntity.ok(lichService.getPhongById(id));
    }

    @PutMapping("/phong/{id}")
    public ResponseEntity<PhongAdminResponseDTO> updatePhong(
            @PathVariable UUID id,
            @Valid @RequestBody PhongAdminRequestDTO request) {
        return ResponseEntity.ok(lichService.updatePhong(id, request));
    }

    @DeleteMapping("/phong/{id}")
    public ResponseEntity<Void> deletePhong(@PathVariable UUID id) {
        lichService.deletePhong(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/phong/batch")
    public ResponseEntity<Void> deletePhongBatch(@RequestBody List<UUID> ids) {
        lichService.deletePhongAllByList(ids);
        return ResponseEntity.noContent().build();
    }

    // ==================== GIO HOC ====================

    @GetMapping("/gio-hoc")
    public ResponseEntity<List<GioHocAdminResponseDTO>> getAllGioHoc() {
        return ResponseEntity.ok(lichService.getAllGioHoc());
    }

    @GetMapping("/gio-hoc/{id}")
    public ResponseEntity<GioHocAdminResponseDTO> getGioHocById(@PathVariable UUID id) {
        return ResponseEntity.ok(lichService.getGioHocById(id));
    }

    @GetMapping("/gio-hoc/search-name")
    public ResponseEntity<List<GioHocAdminResponseDTO>> searchGioHocByName(
            @RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(lichService.searchGioHocByTenGioHoc(keyword));
    }

    @PostMapping("/gio-hoc")
    public ResponseEntity<GioHocAdminResponseDTO> createGioHoc(
            @Valid @RequestBody GioHocAdminRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lichService.createGioHoc(dto));
    }

    @PutMapping("/gio-hoc/{id}")
    public ResponseEntity<GioHocAdminResponseDTO> updateGioHoc(
            @PathVariable UUID id,
            @Valid @RequestBody GioHocAdminRequestDTO dto) {
        return ResponseEntity.ok(lichService.updateGioHoc(id, dto));
    }

    @DeleteMapping("/gio-hoc/{id}")
    public ResponseEntity<Void> deleteGioHoc(@PathVariable UUID id) {
        lichService.deleteGioHoc(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/gio-hoc/delete-list")
    public ResponseEntity<Void> deleteGioHocList(@RequestParam List<UUID> ids) {
        lichService.deleteGioHocAllByList(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/gio-hoc/import-excel")
    public ResponseEntity<?> importGioHocExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(lichService.importGioHocFromExcel(file));
    }
}
