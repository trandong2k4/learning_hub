package com.university.repository.admin;

import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO.UserView;
import com.university.entity.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsersAdminRepository extends JpaRepository<Users, UUID> {

    Users findByEmail(String keyword);

    boolean existsByUserName(String usersname);

    @Query("SELECT u.userName FROM Users u")
    List<String> findAllUserNames();

    @Query("""
             SELECT new com.university.dto.response.admin.UsersAdminResponseDTO(
                 u.id,
                 u.userName,
                 u.passWord,
                 u.email,
                 u.cccd,
                 u.hoTen,
                 u.diaChi,
                 u.gioiTinh,
                 u.ngaySinh,
                 u.soDienThoai,
                 u.trangThai,
                 u.ghiChu,
                 u.createAt,
                 u.updateAt
             )
             FROM Users u
             WHERE u.userName = :username
            """)
    UsersAdminResponseDTO findByUserName(@Param("username") String username);

    @Query("SELECT u.userName FROM Users u")
    List<String> findAllUserName();

    @Query("""
                SELECT r.maRole
                FROM Users u
                JOIN u.dUserRoles ur
                JOIN ur.role r
                WHERE u.id = :userId
            """)
    List<String> findALlNameRoleByUserId(@Param("userId") UUID userId);

    @Query("""
             SELECT new com.university.dto.response.admin.UsersAdminResponseDTO(
                 u.id,
                 u.userName,
                 u.passWord,
                 u.email,
                 u.cccd,
                 u.hoTen,
                 u.diaChi,
                 u.gioiTinh,
                 u.ngaySinh,
                 u.soDienThoai,
                 u.trangThai,
                 u.ghiChu,
                 u.createAt,
                 u.updateAt
             )
             FROM Users u
            """)
    List<UsersAdminResponseDTO> FindAllDTO();

    @Query("""
             SELECT new com.university.dto.response.admin.UsersAdminResponseDTO(
                 u.id,
                 u.userName,
                 u.passWord,
                 u.email,
                 u.cccd,
                 u.hoTen,
                 u.diaChi,
                 u.gioiTinh,
                 u.ngaySinh,
                 u.soDienThoai,
                 u.trangThai,
                 u.ghiChu,
                 u.createAt,
                 u.updateAt
             )
             FROM Users u
             WHERE u.id = :usersId
            """)
    UsersAdminResponseDTO findUsersById(@Param("usersId") UUID usersId);

    @Query("""
            SELECT new com.university.dto.response.admin.UsersAdminResponseDTO(
                 u.id,
                 u.userName,
                 u.passWord,
                 u.email,
                 u.cccd,
                 u.hoTen,
                 u.diaChi,
                 u.gioiTinh,
                 u.ngaySinh,
                 u.soDienThoai,
                 u.trangThai,
                 u.ghiChu,
                 u.createAt,
                 u.updateAt
             )
             FROM Users u
             WHERE LOWER(u.hoTen) LIKE LOWER(CONCAT('%',:keyword,'%'))
            """)
    List<UsersAdminResponseDTO> findUsersByHoTen(@Param("keyword") String keyword);

    @Query("""
             SELECT
                 u.id as id,
                 u.userName as userName ,
                 u.email as email,
                 u.hoTen as hoTen,
                 u.diaChi as diaChi,
                 u.trangThai as trangThai
             FROM Users u
             WHERE u.id = :usersId
            """)
    UserView findByView(@Param("usersId") UUID usersId);

    @Query(" DELETE FROM Users")
    void deleteUsersAll();

}
