package com.university.controller.admin;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.university.dto.request.admin.UsersAdminRequestDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO.UserView;
import com.university.service.admin.UsersAdminService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UsersAdminController {

    private final UsersAdminService usersAdminService;

    @PostMapping
    public ResponseEntity<UsersAdminResponseDTO> create(@Valid @RequestBody UsersAdminRequestDTO dto) {
        return ResponseEntity.ok(usersAdminService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<UsersAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(usersAdminService.getAll());
    }

    @GetMapping("/list-role")
    public ResponseEntity<List<String>> getdSNameRole(@RequestParam("userId") UUID id) {
        return ResponseEntity.ok(usersAdminService.dSNameRoleUSers(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(usersAdminService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UsersAdminResponseDTO>> getByHoTen(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(usersAdminService.getByHoTen(keyword));
    }

    @GetMapping("/users-view")
    public ResponseEntity<UserView> getByView(@PathVariable UUID id) {
        return ResponseEntity.ok(usersAdminService.getByView(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsersAdminResponseDTO> update(@Valid @PathVariable UUID id,
            @RequestBody UsersAdminRequestDTO dto) {
        return ResponseEntity.ok(usersAdminService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        usersAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}