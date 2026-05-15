package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.ThongBaoAdminRequestDTO;
import com.university.dto.response.admin.ThongBaoAdminResponseDTO;
import com.university.service.admin.ThongBaoAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/thong-bao")
@RequiredArgsConstructor
@RequirePermission("ADMIN_NOTIFICATION_VIEW")
public class ThongBaoAdminController {

    private final ThongBaoAdminService thongBaoAdminService;

    @PostMapping
    public ResponseEntity<ThongBaoAdminResponseDTO> create(
            @Valid @RequestBody ThongBaoAdminRequestDTO request) {
        ThongBaoAdminResponseDTO response = thongBaoAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/send")
    public ResponseEntity<ThongBaoAdminResponseDTO> send(
            @Valid @RequestBody ThongBaoAdminRequestDTO request) {
        ThongBaoAdminResponseDTO response = thongBaoAdminService.send(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ThongBaoAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(thongBaoAdminService.getAll());
    }

    @GetMapping("/sender/{usersId}")
    public ResponseEntity<List<ThongBaoAdminResponseDTO>> getAllBySender(@PathVariable UUID usersId) {
        return ResponseEntity.ok(thongBaoAdminService.getAllBySender(usersId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThongBaoAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(thongBaoAdminService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThongBaoAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ThongBaoAdminRequestDTO request) {
        return ResponseEntity.ok(thongBaoAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        thongBaoAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-list")
    public ResponseEntity<Void> deleteList(@RequestBody List<UUID> ids) {
        thongBaoAdminService.deleteAllByList(ids);
        return ResponseEntity.noContent().build();
    }
}
