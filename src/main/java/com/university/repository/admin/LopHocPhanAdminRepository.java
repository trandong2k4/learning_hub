package com.university.repository.admin;

import com.university.dto.response.admin.LopHocPhanAdminResponseDTO;
import com.university.entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LopHocPhanAdminRepository extends JpaRepository<LopHocPhan, UUID> {

    boolean existsByMaLopHocPhan(String maLopHocPhan);

    boolean existsByMaLopHocPhanAndIdNot(String maLopHocPhan, UUID id);

    @Query("""
            SELECT new com.university.dto.response.admin.LopHocPhanAdminResponseDTO(
                l.id,
                l.maLopHocPhan,
                l.soLuongToiDa,
                COUNT(d.id),
                l.trangThai,
                l.hanDangKy,
                l.hanHuy,
                hk.id,
                hk.maHocKi,
                hk.tenHocKi,
                mh.id,
                mh.maMonHoc,
                mh.tenMonHoc,
                mh.soTinChi
            )
            FROM LopHocPhan l
            JOIN l.hocKi hk
            JOIN l.monHoc mh
            LEFT JOIN l.dDangKyTinChis d
            GROUP BY l.id, l.maLopHocPhan, l.soLuongToiDa, l.trangThai, l.hanDangKy, l.hanHuy,
                hk.id, hk.maHocKi, hk.tenHocKi, mh.id, mh.maMonHoc, mh.tenMonHoc, mh.soTinChi
            ORDER BY hk.maHocKi DESC, l.maLopHocPhan ASC
            """)
    List<LopHocPhanAdminResponseDTO> findAllDTO();

    @Query("""
            SELECT new com.university.dto.response.admin.LopHocPhanAdminResponseDTO(
                l.id,
                l.maLopHocPhan,
                l.soLuongToiDa,
                COUNT(d.id),
                l.trangThai,
                l.hanDangKy,
                l.hanHuy,
                hk.id,
                hk.maHocKi,
                hk.tenHocKi,
                mh.id,
                mh.maMonHoc,
                mh.tenMonHoc,
                mh.soTinChi
            )
            FROM LopHocPhan l
            JOIN l.hocKi hk
            JOIN l.monHoc mh
            LEFT JOIN l.dDangKyTinChis d
            WHERE l.id = :id
            GROUP BY l.id, l.maLopHocPhan, l.soLuongToiDa, l.trangThai, l.hanDangKy, l.hanHuy,
                hk.id, hk.maHocKi, hk.tenHocKi, mh.id, mh.maMonHoc, mh.tenMonHoc, mh.soTinChi
            """)
    Optional<LopHocPhanAdminResponseDTO> findDTOById(@Param("id") UUID id);

    @Query("""
            SELECT new com.university.dto.response.admin.LopHocPhanAdminResponseDTO(
                l.id,
                l.maLopHocPhan,
                l.soLuongToiDa,
                COUNT(d.id),
                l.trangThai,
                l.hanDangKy,
                l.hanHuy,
                hk.id,
                hk.maHocKi,
                hk.tenHocKi,
                mh.id,
                mh.maMonHoc,
                mh.tenMonHoc,
                mh.soTinChi
            )
            FROM LopHocPhan l
            JOIN l.hocKi hk
            JOIN l.monHoc mh
            LEFT JOIN l.dDangKyTinChis d
            WHERE hk.id = :hocKiId
            GROUP BY l.id, l.maLopHocPhan, l.soLuongToiDa, l.trangThai, l.hanDangKy, l.hanHuy,
                hk.id, hk.maHocKi, hk.tenHocKi, mh.id, mh.maMonHoc, mh.tenMonHoc, mh.soTinChi
            ORDER BY l.maLopHocPhan ASC
            """)
    List<LopHocPhanAdminResponseDTO> findAllByHocKiIdDTO(@Param("hocKiId") UUID hocKiId);

    @Query("""
            SELECT new com.university.dto.response.admin.LopHocPhanAdminResponseDTO(
                l.id,
                l.maLopHocPhan,
                l.soLuongToiDa,
                COUNT(d.id),
                l.trangThai,
                l.hanDangKy,
                l.hanHuy,
                hk.id,
                hk.maHocKi,
                hk.tenHocKi,
                mh.id,
                mh.maMonHoc,
                mh.tenMonHoc,
                mh.soTinChi
            )
            FROM LopHocPhan l
            JOIN l.hocKi hk
            JOIN l.monHoc mh
            LEFT JOIN l.dDangKyTinChis d
            WHERE mh.id = :monHocId
            GROUP BY l.id, l.maLopHocPhan, l.soLuongToiDa, l.trangThai, l.hanDangKy, l.hanHuy,
                hk.id, hk.maHocKi, hk.tenHocKi, mh.id, mh.maMonHoc, mh.tenMonHoc, mh.soTinChi
            ORDER BY hk.maHocKi DESC, l.maLopHocPhan ASC
            """)
    List<LopHocPhanAdminResponseDTO> findAllByMonHocIdDTO(@Param("monHocId") UUID monHocId);

    @Query("""
            SELECT COUNT(d.id)
            FROM DangKyTinChi d
            WHERE d.lopHocPhan.id = :lopHocPhanId
            """)
    long countDangKyByLopHocPhanId(@Param("lopHocPhanId") UUID lopHocPhanId);

    void deleteAllByIdIn(List<UUID> ids);

    List<LopHocPhan> findAllByHocKiId(UUID hocKiId);

    List<LopHocPhan> findAllByMonHocId(UUID monHocId);

    boolean existsByMonHocId(UUID monHocId);
}
