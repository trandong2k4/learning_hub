package com.university.repository.admin;

import com.university.dto.response.admin.HocVienAdminResponseDTO;
import com.university.entity.HocVien;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HocVienAdminRepository extends JpaRepository<HocVien, UUID> {

    List<HocVienAdminResponseDTO.HocVienView> findAllProjectedBy();

    Optional<HocVien> findByMaHocVien(String maHocVien);

    Optional<HocVien> findByUsersId(UUID usersId);

    @Query("SELECT COUNT(hv) FROM HocVien hv WHERE hv.ngayTotNghiep IS NULL")
    long countByNgayTotNghiepIsNull();

    @Query("SELECT COUNT(hv) FROM HocVien hv WHERE hv.ngayTotNghiep IS NOT NULL")
    long countByNgayTotNghiepIsNotNull();

    @Query("SELECT n.tenNganh, COUNT(s.id) FROM HocVien s JOIN s.nganh n WHERE s.nganh IS NOT NULL GROUP BY n.tenNganh")
    List<Object[]> countByNganhRaw();

    @Query("SELECT EXTRACT(YEAR FROM s.ngayNhapHoc), COUNT(s.id) FROM HocVien s WHERE s.ngayNhapHoc IS NOT NULL GROUP BY EXTRACT(YEAR FROM s.ngayNhapHoc)")
    List<Object[]> countByNamNhapHocRaw();

    void deleteAllByIdIn(List<UUID> ids);

    boolean existsByUsersId(UUID usersId);

    @Query("SELECT DISTINCT hv.users.id FROM HocVien hv WHERE hv.users.id IN :userIds")
    List<UUID> findUserIdsAssignedToHocVien(@Param("userIds") List<UUID> userIds);

    List<HocVien> findAllByNganhId(UUID nganhId);

    @Query("SELECT hv.maHocVien FROM HocVien hv")
    List<String> findAllMaHocVien();

    boolean existsByMaHocVien(String maHocVien);

    boolean existsByMaHocVienAndIdNot(String maHocVien, UUID id);

    @Query("SELECT hv.users.id FROM HocVien hv WHERE hv.users IS NOT NULL")
    List<UUID> findAllUsersId();

    @Query("""
            SELECT new com.university.dto.response.admin.HocVienAdminResponseDTO(
                hv.id,
                hv.maHocVien,
                hv.ngayNhapHoc,
                hv.ngayTotNghiep,
                n.id as nganhId,
                n.tenNganh,
                u.hoTen as tenNhanVien,
                u.userName,
                u.email,
                u.cccd,
                u.diaChi,
                u.soDienThoai,
                u.gioiTinh,
                u.ngaySinh,
                u.id as usersId,
                u.trangThai,
                u.ghiChu
            )
            FROM HocVien hv
            JOIN hv.users u
            JOIN hv.nganh n
            """)
    List<HocVienAdminResponseDTO> findAllWithDetails();
}
