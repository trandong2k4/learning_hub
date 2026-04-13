package com.university.repository.admin;

import com.university.entity.RolePermissions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RolePermissionsAdminRepository extends JpaRepository<RolePermissions, UUID> {

        boolean existsByRoleId(UUID roleId);

        boolean existsByPermissionsId(UUID permissionsId);

}
