package com.university.mapper.admin;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.university.dto.response.admin.UsersRoleAdminResponseDTO;
import com.university.entity.Role;
import com.university.entity.UserRole;
import com.university.entity.Users;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRoleAdminMapper {

    public UserRole toEntity(Role r, Users u) {
        UserRole userRole = new UserRole();
        userRole.setUsers(u);
        userRole.setRole(r);
        userRole.setCreatedAt(LocalDateTime.now());
        userRole.setUpdatedAt(LocalDateTime.now());
        return userRole;
    }

    public void updateEntity(UserRole ur, Role r, Users u) {
        ur.setRole(r);
        ur.setUsers(u);
        ur.setUpdatedAt(LocalDateTime.now());
    }

    public UsersRoleAdminResponseDTO toResponseDTO(UserRole entity) {
        UsersRoleAdminResponseDTO dto = new UsersRoleAdminResponseDTO();

        dto.setId(entity.getId());
        dto.setUserId(entity.getUsers().getId());
        dto.setRoleId(entity.getRole().getId());

        // ⚠️ sửa đúng tên field trong Users
        dto.setUserName(entity.getUsers().getUsername());

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        dto.setMaRole(entity.getRole().getMaRole());

        return dto;
    }
}