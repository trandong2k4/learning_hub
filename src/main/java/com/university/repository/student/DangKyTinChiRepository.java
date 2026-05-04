package com.university.repository.student;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.university.dto.response.student.DangKyTinChiResponseDTO;
import com.university.entity.DangKyTinChi;
import com.university.enums.TrangThaiLHP;

public interface DangKyTinChiRepository extends JpaRepository<DangKyTinChi, UUID> {

    boolean existsByHocVienIdAndLopHocPhanId(UUID hocVienId, UUID lopHocPhanId);

    int countByLopHocPhanId(UUID lopHocPhanId);

    @Query("""
            SELECT SUM(lhp.monHoc.soTinChi)
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            WHERE d.hocVien.id = :hocVienId
            """)
    Integer sumTinChiByHocVien(@Param("hocVienId") UUID hocVienId);

    @Query("""
            SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
            FROM DangKyTinChi d
            WHERE d.hocVien.id = :hocVienId
            AND d.lopHocPhan.monHoc.id = :monHocId
            """)
    boolean daHocMon(@Param("hocVienId") UUID hocVienId, @Param("monHocId") UUID monHocId);

    @Query("""
            SELECT CASE WHEN COUNT(mtq) = 0 THEN true ELSE false END
            FROM MonHocTienQuyet mtq
            JOIN mtq.monHoc mh
            WHERE mh.id = :monHocId
            AND NOT EXISTS (
                SELECT 1 FROM DangKyTinChi d
                JOIN d.lopHocPhan lhp
                WHERE d.hocVien.id = :hocVienId
                AND lhp.monHoc.id = mtq.monTienQuyet.id
            )
            """)
    boolean daHocMonTienQuyet(@Param("hocVienId") UUID hocVienId, @Param("monHocId") UUID monHocId);

    @Query("""
            SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.dLichs lich
            JOIN lich.gioHoc gh
            WHERE d.hocVien.id = :hocVienId
            AND EXISTS (
                SELECT 1 FROM LopHocPhan l2
                JOIN l2.dLichs lich2
                JOIN lich2.gioHoc gh2
                WHERE l2.id = :lopHocPhanId
                AND FUNCTION('DAYOFWEEK', lich.ngayHoc) = FUNCTION('DAYOFWEEK', lich2.ngayHoc)
                AND gh.thoiGianBatDau < gh2.thoiGianKetThuc
                AND gh.thoiGianKetThuc > gh2.thoiGianBatDau
            )
            """)
    boolean existsTrungLichFull(@Param("hocVienId") UUID hocVienId, @Param("lopHocPhanId") UUID lopHocPhanId);

    @Query("""
            SELECT new com.university.dto.response.student.DangKyTinChiResponseDTO(
                d.id,
                d.createdAt,
                d.hocVien.id,
                d.hocVien.maHocVien,
                lhp.id,
                lhp.maLopHocPhan,
                lhp.monHoc.id,
                lhp.monHoc.maMonHoc,
                lhp.monHoc.soTinChi
            )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            WHERE d.hocVien.id = :hocVienId
            """)
    List<DangKyTinChiResponseDTO> findDangKyTinChiResponseDTOByHocVienId(@Param("hocVienId") UUID hocVienId);

    @Query("""
            SELECT new com.university.dto.response.student.DangKyTinChiResponseDTO(
                d.id,
                d.createdAt,
                d.hocVien.id,
                d.hocVien.maHocVien,
                lhp.id,
                lhp.maLopHocPhan,
                lhp.monHoc.id,
                lhp.monHoc.maMonHoc,
                lhp.monHoc.soTinChi
            )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            WHERE d.id = :id
            """)
    Optional<DangKyTinChiResponseDTO> findDangKyTinChiResponseDTOById(@Param("id") UUID id);

    @Query("""
            SELECT d
            FROM DangKyTinChi d
            JOIN FETCH d.lopHocPhan lhp
            JOIN FETCH lhp.monHoc mh
            JOIN FETCH lhp.hocKi hk
            WHERE d.hocVien.id = :hocVienId
            AND lhp.trangThai = :trangThai
            """)
    List<DangKyTinChi> findByHocVienIdAndTrangThai(
            @Param("hocVienId") UUID hocVienId,
            @Param("trangThai") TrangThaiLHP trangThai);
}
