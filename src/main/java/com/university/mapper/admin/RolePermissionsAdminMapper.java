package com.university.mapper.admin;

import org.springframework.stereotype.Component;
import com.university.entity.Permissions;
import com.university.entity.Role;
import com.university.entity.RolePermissions;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RolePermissionsAdminMapper {

    public RolePermissions toEntity(Role r, Permissions p) {
        RolePermissions rolePermissions = new RolePermissions(null, r, p);
        return rolePermissions;
    }

    public void updateEntity(RolePermissions rp, Role r, Permissions p) {
        rp.setRole(r);
        rp.setPermissions(p);
    }
}
