package com.university.repository.admin;

import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO.UserView;
import com.university.dto.response.auth.AuthResponseDTO;
import com.university.dto.response.auth.LoginResponseDTO.UserLoginProjection;
import com.university.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersAdminRepository extends JpaRepository<Users, UUID> {

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    @Query("""
                SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
                FROM Users u
                WHERE u.id = :id
                AND (
                    u.id IN (SELECT nv.users.id FROM NhanVien nv WHERE nv.users IS NOT NULL)
                    OR u.id IN (SELECT hv.users.id FROM HocVien hv WHERE hv.users IS NOT NULL)
                )
            """)
    boolean isUserAlreadyAssigned(@Param("id") UUID id);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUserName(String userName);

    @Query("""
                SELECT
                    u.id as id,
                    u.userName as userName,
                    u.passWord as password,
                    u.hoTen as hoTen,
                    u.trangThai as trangThai,
                    u.ghiChu as ghiChu
                FROM Users u
                WHERE u.userName = :username
            """)
    Optional<UserLoginProjection> findByUserLoginProjection(@Param("username") String username);

    @Query("SELECT u.userName FROM Users u")
    List<String> findAllUserNames();

    @Query("SELECT u.cccd FROM Users u WHERE u.cccd IS NOT NULL")
    List<String> findAllCccds();

    @Query("""
             SELECT
                 u.id as id,
                 u.userName as userName,
                 u.passWord as passWord,
                 u.email as email,
                 u.cccd as cccd,
                 u.hoTen as hoTen,
                 u.diaChi as diaChi,
                 u.gioiTinh as gioiTinh,
                 u.ngaySinh as ngaySinh,
                 u.soDienThoai as soDienThoai,
                 u.trangThai as trangThai,
                 u.ghiChu as ghiChu,
                 u.createAt as createAt,
                 u.updateAt as updateAt
             FROM Users u
             WHERE u.userName = :username
            """)
    UsersAdminResponseDTO.UsersBasicProjection findByUserNameDTO(@Param("username") String username);

    @Query("""
                SELECT DISTINCT new com.university.dto.response.auth.AuthResponseDTO(r.maRole, p.maPermissions)
                FROM Users u
                JOIN u.dUserRoles ur
                JOIN ur.role r
                LEFT JOIN r.dRolePermissions rp
                LEFT JOIN rp.permissions p
                WHERE u.id = :userId
            """)
    List<AuthResponseDTO> findAllRoleAndPermissionsByUserId(@Param("userId") UUID userId);

    @Query("""
                SELECT r.maRole
                FROM Users u
                JOIN u.dUserRoles ur
                JOIN ur.role r
                WHERE u.id = :userId
            """)
    List<String> findAllRoleByUserId(@Param("userId") UUID userId);

    @Query("""
                            SELECT u
                            FROM Users u
                            LEFT JOIN FETCH u.dUserRoles d
                            LEFT JOIN FETCH d.role r
            WHERE u.userName = :username
                        """)
    Optional<Users> findByUsernameWithRoles(String username);

    @Query("""
             SELECT
                 u.id as id,
                 u.userName as userName,
                 u.passWord as passWord,
                 u.email as email,
                 u.cccd as cccd,
                 u.hoTen as hoTen,
                 u.diaChi as diaChi,
                 u.gioiTinh as gioiTinh,
                 u.ngaySinh as ngaySinh,
                 u.soDienThoai as soDienThoai,
                 u.trangThai as trangThai,
                 u.ghiChu as ghiChu,
                 u.createAt as createAt,
                 u.updateAt as updateAt
             FROM Users u
            """)
    List<UsersAdminResponseDTO.UsersBasicProjection> findAllDTO();

    @Query("""
             SELECT
                 u.id as id,
                 u.userName as userName,
                 u.passWord as passWord,
                 u.email as email,
                 u.cccd as cccd,
                 u.hoTen as hoTen,
                 u.diaChi as diaChi,
                 u.gioiTinh as gioiTinh,
                 u.ngaySinh as ngaySinh,
                 u.soDienThoai as soDienThoai,
                 u.trangThai as trangThai,
                 u.ghiChu as ghiChu,
                 u.createAt as createAt,
                 u.updateAt as updateAt
             FROM Users u
             WHERE u.id = :usersId
            """)
    UsersAdminResponseDTO.UsersBasicProjection findUsersById(@Param("usersId") UUID usersId);

    @Query("""
            SELECT
                 u.id as id,
                 u.userName as userName,
                 u.passWord as passWord,
                 u.email as email,
                 u.cccd as cccd,
                 u.hoTen as hoTen,
                 u.diaChi as diaChi,
                 u.gioiTinh as gioiTinh,
                 u.ngaySinh as ngaySinh,
                 u.soDienThoai as soDienThoai,
                 u.trangThai as trangThai,
                 u.ghiChu as ghiChu,
                 u.createAt as createAt,
                 u.updateAt as updateAt
             FROM Users u
             WHERE LOWER(u.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<UsersAdminResponseDTO.UsersBasicProjection> findUsersByHoTen(@Param("keyword") String keyword);

    @Query("""
             SELECT
                 u.id as id,
                 u.userName as userName,
                 u.passWord as passWord,
                 u.email as email,
                 u.cccd as cccd,
                 u.hoTen as hoTen,
                 u.diaChi as diaChi,
                 u.gioiTinh as gioiTinh,
                 u.ngaySinh as ngaySinh,
                 u.soDienThoai as soDienThoai,
                 u.trangThai as trangThai,
                 u.ghiChu as ghiChu
             FROM Users u
             WHERE u.id = :usersId
            """)
    UserView findByView(@Param("usersId") UUID usersId);

    @Query("""
            SELECT
                u.id as id,
                u.userName as userName,
                u.passWord as passWord,
                u.email as email,
                u.cccd as cccd,
                u.hoTen as hoTen,
                u.diaChi as diaChi,
                u.gioiTinh as gioiTinh,
                u.ngaySinh as ngaySinh,
                u.soDienThoai as soDienThoai,
                u.trangThai as trangThai,
                u.ghiChu as ghiChu,
                u.createAt as createAt,
                u.updateAt as updateAt
            FROM Users u
            WHERE u.id NOT IN (
                SELECT nv.users.id FROM NhanVien nv WHERE nv.users IS NOT NULL
            )
            AND u.id NOT IN (
                SELECT hv.users.id FROM HocVien hv WHERE hv.users IS NOT NULL
            )
            ORDER BY u.hoTen ASC
            """)
    List<UsersAdminResponseDTO.UsersBasicProjection> findAllUsersNotAssigned();

    void deleteAllByIdIn(List<UUID> ids);

    boolean existsByEmailAndIdNot(String email, UUID id);

    boolean existsByCccd(String cccd);

    boolean existsByCccdAndIdNot(String cccd, UUID id);

    boolean existsByUserNameAndIdNot(String userName, UUID id);

    @Query("""
                SELECT u.id
                FROM Users u
                JOIN u.dUserRoles ur
                JOIN ur.role r
                WHERE r.id = :roleId
            """)
    List<UUID> findUserIdsByRoleId(@Param("roleId") UUID roleId);

    @Query("""
            SELECT r.maRole
            FROM Users u
            LEFT JOIN u.dUserRoles d
            LEFT JOIN d.role r
            WHERE u.id = :userId
            """)
    List<String> findRolesByUserId(@Param("userId") UUID userId);

    @Query("""
            SELECT u.id, r.maRole
            FROM Users u
            LEFT JOIN u.dUserRoles d
            LEFT JOIN d.role r
            """)
    List<Object[]> findAllUserIdAndRoles();
}