package com.university.repository.admin;

import com.university.dto.response.admin.UsersRoleAdminResponseDTO;
import com.university.entity.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
        List<UsersRoleAdminResponseDTO> getAll();
}
