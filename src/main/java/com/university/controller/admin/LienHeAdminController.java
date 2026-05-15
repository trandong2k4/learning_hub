package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.LienHeAdminRequestDTO;
import com.university.dto.response.admin.LienHeAdminResponseDTO;
import com.university.service.admin.LienHeAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/lien-he")
@RequiredArgsConstructor
@RequirePermission("ADMIN_CONTACT_VIEW")
public class LienHeAdminController {

    private final LienHeAdminService lienHeAdminService;

    @PostMapping
    public ResponseEntity<LienHeAdminResponseDTO> create(
            @Valid @RequestBody LienHeAdminRequestDTO request) {
        LienHeAdminResponseDTO response = lienHeAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LienHeAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(lienHeAdminService.getAll());
    }

    @GetMapping("/khoa/{khoaId}")
    public ResponseEntity<List<LienHeAdminResponseDTO>> getAllByKhoa(@PathVariable UUID khoaId) {
        return ResponseEntity.ok(lienHeAdminService.getAllByKhoa(khoaId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LienHeAdminResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(lienHeAdminService.search(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LienHeAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(lienHeAdminService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LienHeAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody LienHeAdminRequestDTO request) {
        return ResponseEntity.ok(lienHeAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        lienHeAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-list")
    public ResponseEntity<Void> deleteList(@RequestBody List<UUID> ids) {
        lienHeAdminService.deleteAllByList(ids);
        return ResponseEntity.noContent().build();
    }
}
