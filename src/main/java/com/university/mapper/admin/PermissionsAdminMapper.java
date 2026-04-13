package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.PermissionsAdminRequestDTO;
import com.university.dto.response.admin.PermissionsAdminResponseDTO;
import com.university.entity.Permissions;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermissionsAdminMapper {

    public Permissions toEntity(PermissionsAdminRequestDTO dto) {
        Permissions p = new Permissions();
        p.setMaPermissions(dto.getMaPermissions());
        p.setMoTa(dto.getMoTa());
        return p;
    }

    public void upDateEntity(Permissions p, PermissionsAdminRequestDTO dto) {
        p.setMaPermissions(dto.getMaPermissions());
        p.setMoTa(dto.getMoTa());
    }

    public PermissionsAdminResponseDTO toResponseDTO(Permissions entity) {
        PermissionsAdminResponseDTO p = new PermissionsAdminResponseDTO();
        p.setId(entity.getId());
        p.setMaPermissions(entity.getMaPermissions());
        p.setMoTa(entity.getMoTa());
        return p;
    }
}
