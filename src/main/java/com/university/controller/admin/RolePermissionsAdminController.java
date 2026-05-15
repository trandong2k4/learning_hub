package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.RolePermissionsAdminRequestDTO;
import com.university.dto.response.admin.RolePermissionsAdminResponseDTO;
import com.university.service.admin.RolePermissionsAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/role-permissions")
@RequiredArgsConstructor
@RequirePermission("ADMIN_PERMISSIONS_VIEW")
public class RolePermissionsAdminController {

    private final RolePermissionsAdminService rolePermissionsAdminService;

    @PostMapping
    public ResponseEntity<RolePermissionsAdminResponseDTO> create(
            @Valid @RequestBody RolePermissionsAdminRequestDTO request) {
        RolePermissionsAdminResponseDTO response = rolePermissionsAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/by-roleId")
    public ResponseEntity<List<RolePermissionsAdminResponseDTO>> getAllPermissionsByRole(
            @RequestParam("roleId") UUID roleId) {
        return ResponseEntity.ok(rolePermissionsAdminService.getAllByRole(roleId));
    }

    @DeleteMapping("delete-dto")
    public ResponseEntity<Void> deleteRequest(@RequestBody RolePermissionsAdminRequestDTO request) {
        rolePermissionsAdminService.deleteRequest(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        rolePermissionsAdminService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteBatch(@RequestBody List<UUID> ids) {
        List<String> cannotDelete = rolePermissionsAdminService.deleteAllByList(ids);
        if (cannotDelete.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(java.util.Map.of(
            "message", "Một số phân quyền không thể xóa vì vai trò đang được sử dụng",
            "cannotDelete", cannotDelete
        ));
    }

    /**
     * Batch sync permissions for a role.
     * - Unassigns any permissions NOT in grantedIds.
     * - Assigns any permissions in grantedIds NOT currently assigned.
     * - Single DB transaction + single cache evict.
     */
    @PutMapping("/sync/{roleId}")
    public ResponseEntity<Void> syncPermissions(
            @PathVariable UUID roleId,
            @RequestBody List<UUID> grantedIds) {
        rolePermissionsAdminService.syncPermissions(roleId, grantedIds);
        return ResponseEntity.ok().build();
    }
}
