package com.university.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.dto.request.admin.UserRoleAdminRequestDTO;
import com.university.dto.response.admin.UsersRoleAdminResponseDTO;
import com.university.service.admin.UserRoleAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/user-role")
public class UserRoleAdminController {

    private final UserRoleAdminService userRoleAdminService;

    public UserRoleAdminController(UserRoleAdminService userRoleAdminService) {
        this.userRoleAdminService = userRoleAdminService;
    }

    @GetMapping
    public String get() {
        return "Hello, I`m ADMIN! You have access.";
    }

    @PostMapping
    public ResponseEntity<UsersRoleAdminResponseDTO> create(@Valid @RequestBody UserRoleAdminRequestDTO dto) {
        return ResponseEntity.ok(userRoleAdminService.create(dto));
    }
}