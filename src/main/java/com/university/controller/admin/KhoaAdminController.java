package com.university.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.university.dto.request.admin.KhoaAminRequestDTO;
import com.university.dto.response.admin.KhoaAdminResponseDTO;
import com.university.service.admin.KhoaAdminService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/khoa")
@RequiredArgsConstructor
public class KhoaAdminController {

    private final KhoaAdminService khoaService;

    @PostMapping
    public ResponseEntity<KhoaAdminResponseDTO> create(@RequestBody @Valid KhoaAminRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(khoaService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KhoaAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(khoaService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<KhoaAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(khoaService.getAllKhoaDTO());
    }

    @GetMapping("/search")
    public ResponseEntity<List<KhoaAdminResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(khoaService.search(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhoaAdminResponseDTO> update(@PathVariable UUID id,
            @RequestBody @Valid KhoaAminRequestDTO dto) {
        return ResponseEntity.ok(khoaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        khoaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}