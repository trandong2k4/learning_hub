package com.university.repository.admin;

import com.university.dto.response.admin.RolePermissionsAdminResponseDTO;
import com.university.entity.RolePermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query("""
                SELECT new com.university.dto.response.admin.RolePermissionsAdminResponseDTO(
                    rp.id,
                    p.id,
                    p.moTa,
                    CASE
                        WHEN rp.id IS NOT NULL THEN true
                        ELSE false
                    END
                )
                FROM Permissions p
                LEFT JOIN RolePermissions rp
                    ON rp.permissions = p AND rp.role.id = :roleId
                JOIN Role r
                    ON r.id = :roleId
            """)
    List<RolePermissionsAdminResponseDTO> findPermissionsWithStatusByRoleId(@Param("roleId") UUID roleId);

    void deleteAllByIdIn(List<UUID> ids);
}