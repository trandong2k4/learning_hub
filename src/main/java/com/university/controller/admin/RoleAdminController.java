package com.university.controller.admin;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.university.dto.request.admin.RoleAdminRequestDTO;
import com.university.dto.response.admin.RoleAdminResponseDTO;
import com.university.service.admin.RoleAdminService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/role")
@RequiredArgsConstructor
public class RoleAdminController {

    private final RoleAdminService roleAdminService;

    @PostMapping
    public ResponseEntity<RoleAdminResponseDTO> create(@Valid @RequestBody RoleAdminRequestDTO dto) {
        return ResponseEntity.ok(roleAdminService.create(dto));
    }

    @PostMapping("/list")
    public ResponseEntity<List<RoleAdminResponseDTO>> createList(@RequestBody List<RoleAdminRequestDTO> dto) {
        return ResponseEntity.ok(roleAdminService.createListRole(dto));
    }

    @GetMapping
    public ResponseEntity<List<RoleAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(roleAdminService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleAdminService.getRoleById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RoleAdminResponseDTO>> getByNamme(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(roleAdminService.getByMaRole(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleAdminResponseDTO> update(@Valid @PathVariable UUID id,
            @RequestBody RoleAdminRequestDTO dto) {
        return ResponseEntity.ok(roleAdminService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roleAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll() {
        roleAdminService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}