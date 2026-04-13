package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.RoleAdminRequestDTO;
import com.university.dto.response.admin.RoleAdminResponseDTO;
import com.university.entity.Role;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleAdminMapper {

    public Role toEntity(RoleAdminRequestDTO dto) {
        Role role = new Role();
        role.setMaRole(dto.getMaRole());
        role.setMoTa(dto.getMoTa());
        role.setCreatedAt(dto.getCreatedAt());
        return role;
    }

    public void upDateEntity(Role r, RoleAdminRequestDTO dto) {
        r.setMaRole(dto.getMaRole());
        r.setMoTa(dto.getMoTa());
    }

    public RoleAdminResponseDTO toResponseDTO(Role entity) {
        RoleAdminResponseDTO r = new RoleAdminResponseDTO();
        r.setId(entity.getId());
        r.setMaRole(entity.getMaRole());
        r.setMoTa(entity.getMoTa());
        r.setCreatedAt(entity.getCreatedAt());
        return r;
    }
}
