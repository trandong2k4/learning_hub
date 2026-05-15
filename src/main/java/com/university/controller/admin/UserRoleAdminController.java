package com.university.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.UserRoleAdminRequestDTO;
import com.university.dto.response.admin.UsersRoleAdminResponseDTO;
import com.university.service.admin.UserRoleAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/user-role")
@RequiredArgsConstructor
@RequirePermission("ADMIN_ACCOUNT_VIEW")
public class UserRoleAdminController {

    private final UserRoleAdminService userRoleAdminService;

    @GetMapping
    public ResponseEntity<List<UsersRoleAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(userRoleAdminService.getAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UsersRoleAdminResponseDTO>> getByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(userRoleAdminService.getByUserId(userId));
    }

    /**
     * Lấy user-role theo role ID
     */
    @GetMapping("/role/{roleId}")
    public ResponseEntity<UsersRoleAdminResponseDTO> getByRoleId(@PathVariable UUID roleId) {
        return ResponseEntity.ok(userRoleAdminService.getByRoleId(roleId));
    }

    /**
     * Lấy user-role theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsersRoleAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userRoleAdminService.getById(id));
    }

    /**
     * Tạo user-role mới
     */
    @PostMapping
    public ResponseEntity<UsersRoleAdminResponseDTO> create(
            @Valid @RequestBody UserRoleAdminRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userRoleAdminService.create(dto));
    }

    /**
     * Tạo nhiều user-role cùng lúc
     */
    @PostMapping("/create-list")
    public ResponseEntity<List<UsersRoleAdminResponseDTO>> createList(
            @Valid @RequestBody List<UserRoleAdminRequestDTO> dto) {
        return ResponseEntity.ok(userRoleAdminService.createListUserRole(dto));
    }

    /**
     * Xóa user-role theo ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userRoleAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Xóa nhiều user-role
     */
    @DeleteMapping("/delete-list")
    public ResponseEntity<Void> deleteList(@RequestBody List<UUID> ids) {
        userRoleAdminService.deleteAll(ids);
        return ResponseEntity.noContent().build();
    }
}