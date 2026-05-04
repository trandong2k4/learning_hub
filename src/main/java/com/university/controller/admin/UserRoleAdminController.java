package com.university.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.dto.request.admin.UserRoleAdminRequestDTO;
import com.university.dto.response.admin.UsersRoleAdminResponseDTO;
import com.university.service.admin.UserRoleAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/user-role")
@RequiredArgsConstructor
public class UserRoleAdminController {

    private final UserRoleAdminService userRoleAdminService;

    @PostMapping
    public ResponseEntity<UsersRoleAdminResponseDTO> create(
            @Valid @RequestBody UserRoleAdminRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userRoleAdminService.create(dto));
    }

    @PostMapping("/create-list")
    public ResponseEntity<List<UsersRoleAdminResponseDTO>> createList(
            @Valid @RequestBody List<UserRoleAdminRequestDTO> dto) {
        return ResponseEntity.ok(userRoleAdminService.createListUserRole(dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@Valid @PathVariable UUID id) {
        userRoleAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}