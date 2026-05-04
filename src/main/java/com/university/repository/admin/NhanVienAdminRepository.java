package com.university.repository.admin;

import com.university.dto.response.admin.NhanVienAdminResponseDTO;
import com.university.dto.response.admin.PermissionsAdminResponseDTO;
import com.university.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NhanVienAdminRepository extends JpaRepository<NhanVien, UUID> {
    @Query("""
                SELECT COUNT(DISTINCT nv)
                FROM NhanVien nv
                JOIN nv.users us
                JOIN us.dUserRoles ur
                JOIN ur.role r
                WHERE r.maRole = 'LECTURE'
            """)
    Long countGiangVien();

    @Query("SELECT nv.maNhanVien FROM NhanVien nv")
    List<String> findAllMaNhanVien();

    @Query("""
                SELECT new com.university.dto.response.admin.NhanVienAdminResponseDTO(
                    nv.id,
                    nv.maNhanVien,
                    u.hoTen as tenNhanVien,
                    u.gioiTinh,
                    nv.ngayNhanViec,
                    nv.ngayNghiViec,
                    u.id as usersd
            )
                FROM NhanVien nv JOIN nv.users u
            """)
    List<NhanVienAdminResponseDTO> findAllDTO();

    @Query("""
            SELECT new com.university.dto.response.admin.PermissionsAdminResponseDTO(
                p.id,
                p.maPermissions,
                p.moTa
            )
            FROM Permissions p
            """)
    List<PermissionsAdminResponseDTO> getAllPermissionsDTO();

    void deleteAllByIdIn(List<UUID> ids);
}
