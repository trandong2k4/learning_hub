package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.LichSuLienHeAdminRequestDTO;
import com.university.dto.response.admin.LichSuLienHeAdminResponseDTO;
import com.university.service.admin.LichSuLienHeAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/lich-su-lien-he")
@RequiredArgsConstructor
@RequirePermission("ADMIN_CONTACT_VIEW")
public class LichSuLienHeAdminController {

    private final LichSuLienHeAdminService lichSuLienHeAdminService;

    @PostMapping
    public ResponseEntity<LichSuLienHeAdminResponseDTO> create(
            @Valid @RequestBody LichSuLienHeAdminRequestDTO request) {
        LichSuLienHeAdminResponseDTO response = lichSuLienHeAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LichSuLienHeAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(lichSuLienHeAdminService.getAll());
    }

    @GetMapping("/lien-he/{lienHeId}")
    public ResponseEntity<List<LichSuLienHeAdminResponseDTO>> getAllByLienHe(@PathVariable UUID lienHeId) {
        System.out.println("ssss");
        return ResponseEntity.ok(lichSuLienHeAdminService.getAllByLienHe(lienHeId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LichSuLienHeAdminResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(lichSuLienHeAdminService.search(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LichSuLienHeAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(lichSuLienHeAdminService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LichSuLienHeAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody LichSuLienHeAdminRequestDTO request) {
        return ResponseEntity.ok(lichSuLienHeAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        lichSuLienHeAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-list")
    public ResponseEntity<Void> deleteList(@RequestBody List<UUID> ids) {
        lichSuLienHeAdminService.deleteAllByList(ids);
        return ResponseEntity.noContent().build();
    }
}
