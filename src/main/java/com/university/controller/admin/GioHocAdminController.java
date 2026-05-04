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
    public ResponseEntity<List<GioHocAdminResponseDTO>> getById(@PathParam("keyword") String keyword) {
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
}