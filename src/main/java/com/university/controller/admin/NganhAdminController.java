package com.university.controller.admin;

import com.university.dto.request.admin.NganhAminRequestDTO;
import com.university.dto.response.admin.NganhAdminResponseDTO;
import com.university.service.admin.NganhAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/nganh")
@RequiredArgsConstructor
public class NganhAdminController {

    private final NganhAdminService nganhService;

    @PostMapping
    public ResponseEntity<NganhAdminResponseDTO> create(@RequestBody @Valid NganhAminRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nganhService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NganhAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(nganhService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<NganhAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(nganhService.getAllNganhResponseDTO());
    }

    // @GetMapping("/search")
    // public ResponseEntity<List<NganhResponseDTO>> search(@RequestParam String
    // keyword) {
    // return ResponseEntity.ok(nganhService.search(keyword));
    // }

    @PutMapping("/{id}")
    public ResponseEntity<NganhAdminResponseDTO> update(@PathVariable UUID id,
            @RequestBody @Valid NganhAminRequestDTO dto) {
        return ResponseEntity.ok(nganhService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        nganhService.delete(id);
        return ResponseEntity.noContent().build();
    }
}