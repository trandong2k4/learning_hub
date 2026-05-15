package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.ThongBaoNguoiDungAdminRequestDTO;
import com.university.dto.response.admin.ThongBaoNguoiDungAdminResponseDTO;
import com.university.service.admin.ThongBaoNguoiDungAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/thong-bao-nguoi-dung")
@RequiredArgsConstructor
@RequirePermission("ADMIN_NOTIFICATION_VIEW")
public class ThongBaoNguoiDungAdminController {

    private final ThongBaoNguoiDungAdminService thongBaoNguoiDungAdminService;

    @PostMapping
    public ResponseEntity<ThongBaoNguoiDungAdminResponseDTO> create(
            @Valid @RequestBody ThongBaoNguoiDungAdminRequestDTO request) {
        ThongBaoNguoiDungAdminResponseDTO response = thongBaoNguoiDungAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ThongBaoNguoiDungAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(thongBaoNguoiDungAdminService.getAll());
    }

    @GetMapping("/thong-bao/{thongBaoId}")
    public ResponseEntity<List<ThongBaoNguoiDungAdminResponseDTO>> getAllByThongBao(@PathVariable UUID thongBaoId) {
        return ResponseEntity.ok(thongBaoNguoiDungAdminService.getAllByThongBao(thongBaoId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ThongBaoNguoiDungAdminResponseDTO>> getAllByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(thongBaoNguoiDungAdminService.getAllByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThongBaoNguoiDungAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(thongBaoNguoiDungAdminService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThongBaoNguoiDungAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ThongBaoNguoiDungAdminRequestDTO request) {
        return ResponseEntity.ok(thongBaoNguoiDungAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        thongBaoNguoiDungAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-list")
    public ResponseEntity<Void> deleteList(@RequestBody List<UUID> ids) {
        thongBaoNguoiDungAdminService.deleteAllByList(ids);
        return ResponseEntity.noContent().build();
    }
}
