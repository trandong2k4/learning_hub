package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.university.dto.request.admin.UsersAdminRequestDTO;
import com.university.dto.response.admin.BatchDeleteResultDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO.UserView;
import com.university.dto.response.auth.AuthResponseDTO;
import com.university.service.admin.UsersAdminService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@RequirePermission("ADMIN_ACCOUNT_VIEW")
public class UsersAdminController {

    private final UsersAdminService usersAdminService;

    // ── Create ────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<UsersAdminResponseDTO> create(@Valid @RequestBody UsersAdminRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usersAdminService.create(dto));
    }

    // ── Read ──────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<UsersAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(usersAdminService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(usersAdminService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsersAdminResponseDTO>> search(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(usersAdminService.getByHoTen(keyword));
    }

    @GetMapping("/users-view/{id}")
    public ResponseEntity<UserView> getByView(@PathVariable UUID id) {
        return ResponseEntity.ok(usersAdminService.getByView(id));
    }

    @GetMapping("/list-role")
    public ResponseEntity<List<AuthResponseDTO>> listRole(@RequestParam("userId") UUID id) {
        return ResponseEntity.ok(usersAdminService.dSNameRoleUSers(id));
    }

    // ── Update ────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<UsersAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UsersAdminRequestDTO dto) {
        return ResponseEntity.ok(usersAdminService.update(id, dto));
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        usersAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<BatchDeleteResultDTO> deleteBatch(@RequestBody List<UUID> ids) {
        return ResponseEntity.ok(usersAdminService.deleteAllByList(ids));
    }

    // ── Import ────────────────────────────────────────────────────────────────
    @PostMapping("/import")
    public ResponseEntity<ExcelImportResult> importFromExcel(
            @RequestParam("file") MultipartFile file) throws java.io.IOException {
        return ResponseEntity.ok(usersAdminService.importFromExcel(file));
    }
}
