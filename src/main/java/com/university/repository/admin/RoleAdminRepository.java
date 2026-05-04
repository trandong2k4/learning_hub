package com.university.repository.admin;

import com.university.dto.response.admin.RoleAdminResponseDTO;
import com.university.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleAdminRepository extends JpaRepository<Role, UUID> {

    @Query("""
            SELECT new com.university.dto.response.admin.RoleAdminResponseDTO(
                r.id,
                r.maRole,
                r.moTa,
                r.createdAt
            )
            FROM Role r
            """)
    List<RoleAdminResponseDTO> getAllRoleDTO();

    @Query("""
             SELECT new com.university.dto.response.admin.RoleAdminResponseDTO(
                 r.id,
                 r.maRole,
                 r.moTa,
                 r.createdAt
             )
             FROM Role r
             WHERE r.id = :roleId
            """)
    Optional<RoleAdminResponseDTO> findRoleById(@Param("roleId") UUID roleId);

    @Query("""
             SELECT new com.university.dto.response.admin.RoleAdminResponseDTO(
                 r.id,
                 r.maRole,
                 r.moTa,
                 r.createdAt
             )
             FROM Role r
             WHERE LOWER(r.maRole) LIKE LOWER(CONCAT('%',:keyword,'%'))
            """)
    List<RoleAdminResponseDTO> findRoleByMaRole(@Param("keyword") String keyword);

    boolean existsByMaRole(String maRole);

    @Query("""
            SELECT COUNT(r)
            FROM Role r
            LEFT JOIN r.dUserRole ur
            LEFT JOIN r.dRolePermissions rp
            WHERE r.id = :roleId
            AND (ur.id IS NOT NULL OR rp.id IS NOT NULL)
            """)
    long checkRoleUsed(UUID roleId);

    @Query(" DELETE FROM Role")
    void deleteAll();

    void deleteAllByIdIn(List<UUID> ids);
}
