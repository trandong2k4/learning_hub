package com.university.repository.admin;

import com.university.dto.response.admin.UsersRoleAdminResponseDTO;
import com.university.entity.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleAdminRepository extends JpaRepository<UserRole, UUID> {
    boolean existsByRoleId(UUID roleId);

    boolean existsByUsersId(UUID usersId);

    @Query("""
                SELECT new com.university.dto.response.admin.UsersRoleAdminResponseDTO(
                    ur.id,
                    u.id,
                    r.id,
                    u.userName,
                    ur.createdAt,
                    ur.updatedAt,
                    r.maRole
                )
                FROM UserRole ur
                JOIN ur.users u
                JOIN ur.role r
            """)
    List<UsersRoleAdminResponseDTO> getAllDTO();

    UserRole findByUsersId(UUID usersId);

    @Query("""
                SELECT new com.university.dto.response.admin.UsersRoleAdminResponseDTO(
                    ur.id,
                    u.id,
                    r.id,
                    u.userName,
                    ur.createdAt,
                    ur.updatedAt,
                    r.maRole
                )
                FROM UserRole ur
                JOIN ur.users u
                JOIN ur.role r
                WHERE u.id = :usersId
            """)
    List<UsersRoleAdminResponseDTO> findAllDTOByUsersId(@Param("usersId") UUID usersId);

    UserRole findByRoleId(UUID roleId);

    boolean existsByUsersIdAndRoleId(UUID usersId, UUID roleId);

    void deleteByUsersId(UUID usersId);

    void deleteByRoleId(UUID roleId);

    void deleteByUsersIdAndRoleId(UUID usersId, UUID roleId);

    void deleteAllByIdInBatch(Iterable<UUID> ids);

    void deleteAllByIdIn(List<UUID> ids);

    @Query("""
                SELECT p.maPermissions
                FROM UserRole ur
                JOIN ur.role r
                JOIN r.dRolePermissions rp
                JOIN rp.permissions p
                WHERE ur.users.id = :userId
            """)
    List<String> findPermissionsByUserId(UUID userId);
}
