package com.university.repository.admin;

import com.university.entity.RolePermissions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolePermissionsAdminRepository extends JpaRepository<RolePermissions, UUID> {

        // Kiểm tra xem cặp Role và Permission này đã tồn tại chưa
        boolean existsByRoleIdAndPermissionsId(UUID roleId, UUID permissionsId);

        // Tìm kiếm bản ghi cụ thể theo cặp ID
        Optional<RolePermissions> findByRoleIdAndPermissionsId(UUID roleId, UUID permissionsId);

        // Các phương thức đơn lẻ bạn đã viết (nếu vẫn cần thiết)
        boolean existsByRoleId(UUID roleId);

        boolean existsByPermissionsId(UUID permissionsId);
}