package com.university.repository.admin;

import com.university.dto.response.admin.NhanVienAdminResponseDTO;
import com.university.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
                            WHERE r.maRole = 'LECTURER'
                        """)
        Long countGiangVien();

        @Query("SELECT nv.maNhanVien FROM NhanVien nv")
        List<String> findAllMaNhanVien();

        @Query("SELECT nv.users.id FROM NhanVien nv WHERE nv.users IS NOT NULL")
        List<UUID> findAllUsersId();

        @Query("SELECT nv.users.userName FROM NhanVien nv WHERE nv.users IS NOT NULL")
        List<String> findAllUsername();

        boolean existsByMaNhanVien(String maNhanVien);

        boolean existsByMaNhanVienAndIdNot(String maNhanVien, UUID id);

        @Query("""
                        SELECT new com.university.dto.response.admin.NhanVienAdminResponseDTO(
                                nv.id,
                                nv.maNhanVien,
                                u.hoTen,
                                u.gioiTinh,
                                nv.ngayNhanViec,
                                nv.ngayNghiViec,
                                u.id,
                                u.userName,
                                u.email,
                                u.cccd,
                                u.diaChi,
                                u.soDienThoai,
                                u.ngaySinh,
                                u.trangThai,
                                u.ghiChu
                        )
                        FROM NhanVien nv
                        LEFT JOIN nv.users u
                        ORDER BY nv.maNhanVien ASC
                        """)
        List<NhanVienAdminResponseDTO> findAllStaffDTO();

        @Query("""
                        SELECT new com.university.dto.response.admin.NhanVienAdminResponseDTO(
                                nv.id,
                                nv.maNhanVien,
                                u.hoTen,
                                u.gioiTinh,
                                nv.ngayNhanViec,
                                nv.ngayNghiViec,
                                u.id,
                                u.userName,
                                u.email,
                                u.cccd,
                                u.diaChi,
                                u.soDienThoai,
                                u.ngaySinh,
                                u.trangThai,
                                u.ghiChu
                        )
                        FROM NhanVien nv
                        LEFT JOIN nv.users u
                        WHERE nv.id = :id
                        """)
        java.util.Optional<NhanVienAdminResponseDTO> findStaffDTOById(@Param("id") UUID id);

        void deleteAllByIdIn(List<UUID> ids);

        boolean existsByUsersId(UUID usersId);

        boolean existsByIdIn(List<UUID> ids);

        @Query("""
                        SELECT DISTINCT new com.university.dto.response.admin.NhanVienAdminResponseDTO(
                                nv.id,
                                nv.maNhanVien,
                                u.hoTen,
                                u.gioiTinh,
                                nv.ngayNhanViec,
                                nv.ngayNghiViec,
                                u.id,
                                u.userName,
                                u.email,
                                u.cccd,
                                u.diaChi,
                                u.soDienThoai,
                                u.ngaySinh,
                                u.trangThai,
                                u.ghiChu
                        )
                        FROM NhanVien nv
                        JOIN nv.users u
                        JOIN u.dUserRoles ur
                        JOIN ur.role r
                        WHERE r.maRole = 'LECTURER'
                        ORDER BY nv.maNhanVien ASC
                        """)
        List<NhanVienAdminResponseDTO> findAllLecturersDTO();
}
