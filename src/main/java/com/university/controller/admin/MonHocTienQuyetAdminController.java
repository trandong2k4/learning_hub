package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.MonHocTienQuyetAdminRequestDTO;
import com.university.dto.response.admin.MonHocTienQuyetAdminResponseDTO;
import com.university.service.admin.MonHocTienQuyetAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/mon-hoc-tien-quyet")
@RequiredArgsConstructor
@RequirePermission("ADMIN_SUBJECT_VIEW")
public class MonHocTienQuyetAdminController {

    private final MonHocTienQuyetAdminService monHocTienQuyetService;

    @PostMapping
    public ResponseEntity<MonHocTienQuyetAdminResponseDTO> create(
            @Valid @RequestBody MonHocTienQuyetAdminRequestDTO request) {
        MonHocTienQuyetAdminResponseDTO response = monHocTienQuyetService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonHocTienQuyetAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody MonHocTienQuyetAdminRequestDTO request) {
        MonHocTienQuyetAdminResponseDTO response = monHocTienQuyetService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonHocTienQuyetAdminResponseDTO> getById(@PathVariable UUID id) {
        MonHocTienQuyetAdminResponseDTO response = monHocTienQuyetService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MonHocTienQuyetAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(monHocTienQuyetService.getAll());
    }

    @GetMapping("/by-mon-hoc/{monHocId}")
    public ResponseEntity<List<MonHocTienQuyetAdminResponseDTO>> getByMonHocId(@PathVariable UUID monHocId) {
        return ResponseEntity.ok(monHocTienQuyetService.getByMonHocId(monHocId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        monHocTienQuyetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/by-list")
    public ResponseEntity<String> deleteList(@RequestBody List<UUID> ids) {
        monHocTienQuyetService.deleteAllByList(ids);
        return ResponseEntity.ok("Xóa thành công " + ids.size() + " mục");
    }
}
