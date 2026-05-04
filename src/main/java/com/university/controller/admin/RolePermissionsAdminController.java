package com.university.controller.admin;

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
}
