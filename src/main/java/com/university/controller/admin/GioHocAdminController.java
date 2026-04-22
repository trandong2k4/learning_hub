package com.university.controller.admin;

import com.university.dto.request.admin.GioHocAdminRequestDTO;
import com.university.dto.response.admin.GioHocAdminResponseDTO;
import com.university.service.admin.GioHocAdminService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/gio-hoc")
@RequiredArgsConstructor
public class GioHocAdminController {

    private final GioHocAdminService gioHocService;

    @GetMapping
    public ResponseEntity<List<GioHocAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(gioHocService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GioHocAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(gioHocService.getById(id));
    }

    @GetMapping("/search-name")
    public ResponseEntity<List<GioHocAdminResponseDTO>> getById(@PathParam("keyword") String keyword) {
        return ResponseEntity.ok(gioHocService.getByTenGioHoc(keyword));
    }

    @PostMapping
    public ResponseEntity<GioHocAdminResponseDTO> create(@Valid @RequestBody GioHocAdminRequestDTO dto) {
        return ResponseEntity.ok(gioHocService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GioHocAdminResponseDTO> update(@PathVariable UUID id,
            @Valid @RequestBody GioHocAdminRequestDTO dto) {
        return ResponseEntity.ok(gioHocService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        gioHocService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteList(@RequestParam List<UUID> ids) {
        gioHocService.deleteMultiple(ids);
        return ResponseEntity.noContent().build();
    }
}