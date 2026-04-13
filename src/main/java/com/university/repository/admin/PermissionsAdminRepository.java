package com.university.repository.admin;

import com.university.dto.response.admin.PermissionsAdminResponseDTO;
import com.university.entity.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionsAdminRepository extends JpaRepository<Permissions, UUID> {

    @Query("""
            SELECT new com.university.dto.response.admin.PermissionsAdminResponseDTO(
                p.id,
                p.maPermissions,
                p.moTa
            )
            FROM Permissions p
            """)
    List<PermissionsAdminResponseDTO> getAllPermissionsDTO();

    @Query("""
            SELECT new com.university.dto.response.admin.PermissionsAdminResponseDTO(
                p.id,
                p.maPermissions,
                p.moTa
            )
            FROM Permissions p
            WHERE p.id = :permissionsId
            """)
    Optional<PermissionsAdminResponseDTO> findPermissionsById(@Param("roleId") UUID permissionsId);

    @Query("""
            SELECT new com.university.dto.response.admin.PermissionsAdminResponseDTO(
                p.id,
                p.maPermissions,
                p.moTa
            )
            FROM Permissions p
            WHERE LOWER(p.maPermissions) LIKE LOWER(CONCAT('%',:keyword,'%'))
            """)
    List<PermissionsAdminResponseDTO> findPermissionsByMaPermissions(@Param("keyword") String keyword);

    boolean existsByMaPermissions(String maPermissions);

    @Query("SELECT p.maPermissions FROM Permissions p")
    List<String> findAllMaPermissions();

    @Query("""
            SELECT COUNT(p)
            FROM Permissions p
            LEFT JOIN p.dRolePermissions rp
            WHERE p.id = :permissionsId AND (rp.id IS NOT NULL)
            """)
    long checkPermissionsUsed(UUID permissionsId);

    @Modifying
    @Query(" DELETE FROM Permissions p ")
    void deleteAllPermissions();
}
