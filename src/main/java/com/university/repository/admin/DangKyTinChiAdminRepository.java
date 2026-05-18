package com.university.repository.admin;

import com.university.dto.response.admin.DangKyTinChiAdminResponseDTO;
import com.university.dto.response.admin.HocPhiAdminResponseDTO;
import com.university.entity.DangKyTinChi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DangKyTinChiAdminRepository extends JpaRepository<DangKyTinChi, UUID> {

    interface InvoiceCandidateProjection {
        UUID getHocVienId();

        String getMaHocVien();

        String getHocVienName();

        String getHocVienEmail();

        Long getTongSoTinChi();
    }

    @Query("""
            SELECT new com.university.dto.response.admin.DangKyTinChiAdminResponseDTO(
                d.id,
                lhp.id,
                lhp.maLopHocPhan,
                hv.id,
                hv.maHocVien,
                u.id,
                mh.id,
                mh.maMonHoc,
                mh.soTinChi,
                d.createdAt
            )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users u
            ORDER BY d.createdAt DESC
            """)
    List<DangKyTinChiAdminResponseDTO> findAllDTO();

    @Query("""
            SELECT new com.university.dto.response.admin.DangKyTinChiAdminResponseDTO(
                d.id,
                lhp.id,
                lhp.maLopHocPhan,
                hv.id,
                hv.maHocVien,
                u.id,
                mh.id,
                mh.maMonHoc,
                mh.soTinChi,
                d.createdAt
            )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users u
            WHERE d.id = :id
            """)
    Optional<DangKyTinChiAdminResponseDTO> findDTOById(@Param("id") UUID id);

    @Query("""
            SELECT new com.university.dto.response.admin.DangKyTinChiAdminResponseDTO(
                d.id,
                lhp.id,
                lhp.maLopHocPhan,
                hv.id,
                hv.maHocVien,
                u.id,
                mh.id,
                mh.maMonHoc,
                mh.soTinChi,
                d.createdAt
            )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users u
            WHERE hv.id = :hocVienId
            ORDER BY d.createdAt DESC
            """)
    List<DangKyTinChiAdminResponseDTO> findAllByHocVienIdDTO(@Param("hocVienId") UUID hocVienId);

    @Query("""
            SELECT new com.university.dto.response.admin.DangKyTinChiAdminResponseDTO(
                d.id,
                lhp.id,
                lhp.maLopHocPhan,
                hv.id,
                hv.maHocVien,
                u.id,
                mh.id,
                mh.maMonHoc,
                mh.soTinChi,
                d.createdAt
            )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users u
            WHERE lhp.id = :lopHocPhanId
            ORDER BY d.createdAt DESC
            """)
    List<DangKyTinChiAdminResponseDTO> findAllByLopHocPhanIdDTO(@Param("lopHocPhanId") UUID lopHocPhanId);

    boolean existsByHocVien_IdAndLopHocPhan_Id(UUID hocVienId, UUID lopHocPhanId);

    boolean existsByHocVien_IdAndLopHocPhan_IdAndIdNot(UUID hocVienId, UUID lopHocPhanId, UUID id);

    int countByLopHocPhan_Id(UUID lopHocPhanId);

    @Query("""
            SELECT SUM(lhp.monHoc.soTinChi)
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            WHERE d.hocVien.id = :hocVienId
            """)
    Integer sumTinChiByHocVien(@Param("hocVienId") UUID hocVienId);

    @Query("""
            SELECT SUM(lhp.monHoc.soTinChi)
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            WHERE d.hocVien.id = :hocVienId
            AND d.id <> :excludeId
            """)
    Integer sumTinChiByHocVienExcludingId(@Param("hocVienId") UUID hocVienId, @Param("excludeId") UUID excludeId);

    @Query("""
            SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
            FROM DangKyTinChi d
            WHERE d.hocVien.id = :hocVienId
            AND d.lopHocPhan.monHoc.id = :monHocId
            """)
    boolean daHocMon(@Param("hocVienId") UUID hocVienId, @Param("monHocId") UUID monHocId);

    @Query("""
            SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
            FROM DangKyTinChi d
            WHERE d.hocVien.id = :hocVienId
            AND d.lopHocPhan.monHoc.id = :monHocId
            AND d.id <> :excludeId
            """)
    boolean daHocMonExcludingId(
            @Param("hocVienId") UUID hocVienId,
            @Param("monHocId") UUID monHocId,
            @Param("excludeId") UUID excludeId);

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

    @Query(value = """
            SELECT CASE WHEN COUNT(dktc.id) > 0 THEN TRUE ELSE FALSE END
            FROM dang_ky_tin_chi dktc
            JOIN lop_hoc_phan lhp1 ON lhp1.id = dktc.lop_hoc_phan_id
            JOIN lich l1 ON l1.lop_hoc_phan_id = lhp1.id
            JOIN gio_hoc gh1 ON gh1.id = l1.gio_hoc_id
            WHERE dktc.hoc_vien_id = :hocVienId
            AND EXISTS (
                SELECT 1
                FROM lop_hoc_phan lhp2
                JOIN lich l2 ON l2.lop_hoc_phan_id = lhp2.id
                JOIN gio_hoc gh2 ON gh2.id = l2.gio_hoc_id
                WHERE lhp2.id = :lopHocPhanId
                AND EXTRACT(DOW FROM l1.ngay_hoc) = EXTRACT(DOW FROM l2.ngay_hoc)
                AND gh1.thoi_gian_bat_dau < gh2.thoi_gian_ket_thuc
                AND gh1.thoi_gian_ket_thuc > gh2.thoi_gian_bat_dau
            )
            """, nativeQuery = true)
    boolean existsTrungLichFull(@Param("hocVienId") UUID hocVienId, @Param("lopHocPhanId") UUID lopHocPhanId);

    @Query(value = """
            SELECT CASE WHEN COUNT(dktc.id) > 0 THEN TRUE ELSE FALSE END
            FROM dang_ky_tin_chi dktc
            JOIN lop_hoc_phan lhp1 ON lhp1.id = dktc.lop_hoc_phan_id
            JOIN lich l1 ON l1.lop_hoc_phan_id = lhp1.id
            JOIN gio_hoc gh1 ON gh1.id = l1.gio_hoc_id
            WHERE dktc.hoc_vien_id = :hocVienId
            AND dktc.id <> :excludeId
            AND EXISTS (
                SELECT 1
                FROM lop_hoc_phan lhp2
                JOIN lich l2 ON l2.lop_hoc_phan_id = lhp2.id
                JOIN gio_hoc gh2 ON gh2.id = l2.gio_hoc_id
                WHERE lhp2.id = :lopHocPhanId
                AND EXTRACT(DOW FROM l1.ngay_hoc) = EXTRACT(DOW FROM l2.ngay_hoc)
                AND gh1.thoi_gian_bat_dau < gh2.thoi_gian_ket_thuc
                AND gh1.thoi_gian_ket_thuc > gh2.thoi_gian_bat_dau
            )
            """, nativeQuery = true)
    boolean existsTrungLichFullExcludingId(
            @Param("hocVienId") UUID hocVienId,
            @Param("lopHocPhanId") UUID lopHocPhanId,
            @Param("excludeId") UUID excludeId);

    long countByIdIn(List<UUID> ids);

    void deleteAllByIdIn(List<UUID> ids);

    boolean existsByHocVienId(UUID hocVienId);

    @Query("SELECT DISTINCT d.hocVien.id FROM DangKyTinChi d WHERE d.hocVien.id IN :hocVienIds")
    List<UUID> findHocVienIdsHavingDangKy(@org.springframework.data.repository.query.Param("hocVienIds") List<UUID> hocVienIds);

    @Query("""
            SELECT d.id AS id,
                   hv.id AS hocVienId,
                   hv.maHocVien AS hocVienMa,
                   u.hoTen AS hocVienHoTen,
                   lhp.hocKi.id AS hocKiId,
                   lhp.hocKi.maHocKi AS hocKiMa,
                   lhp.hocKi.tenHocKi AS hocKiTen,
                   mh.soTinChi AS soTinChi,
                   mh.soTinChi * 700000.0 AS soTien,
                   d.createdAt AS ngayDangKy
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users u
            ORDER BY d.createdAt DESC
            """)
    List<HocPhiAdminResponseDTO.DangKyTinChiItem> findAllDangKyTinChiWithTien();

    @Query("""
            SELECT d.id AS id,
                   hv.id AS hocVienId,
                   hv.maHocVien AS hocVienMa,
                   u.hoTen AS hocVienHoTen,
                   lhp.hocKi.id AS hocKiId,
                   lhp.hocKi.maHocKi AS hocKiMa,
                   lhp.hocKi.tenHocKi AS hocKiTen,
                   mh.soTinChi AS soTinChi,
                   mh.soTinChi * 700000.0 AS soTien,
                   d.createdAt AS ngayDangKy
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users u
            WHERE lhp.hocKi.id = :hocKiId
            ORDER BY d.createdAt DESC
            """)
    List<HocPhiAdminResponseDTO.DangKyTinChiItem> findDangKyTinChiByHocKi(@Param("hocKiId") UUID hocKiId);

    @Query("""
            SELECT hv.id AS hocVienId,
                   hv.maHocVien AS maHocVien,
                   u.hoTen AS hocVienName,
                   u.email AS hocVienEmail,
                   COALESCE(SUM(mh.soTinChi), 0) AS tongSoTinChi
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users u
            WHERE lhp.hocKi.id = :hocKiId
            GROUP BY hv.id, hv.maHocVien, u.hoTen, u.email
            ORDER BY hv.maHocVien ASC
            """)
    List<InvoiceCandidateProjection> findInvoiceCandidatesByHocKi(@Param("hocKiId") UUID hocKiId);

    @Query("""
            SELECT d.id AS id,
                   hv.id AS hocVienId,
                   hv.maHocVien AS hocVienMa,
                   u.hoTen AS hocVienHoTen,
                   lhp.hocKi.id AS hocKiId,
                   lhp.hocKi.maHocKi AS hocKiMa,
                   lhp.hocKi.tenHocKi AS hocKiTen,
                   mh.soTinChi AS soTinChi,
                   mh.soTinChi * 700000.0 AS soTien,
                   d.createdAt AS ngayDangKy
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users u
            WHERE hv.id = :hocVienId
            ORDER BY d.createdAt DESC
            """)
    List<HocPhiAdminResponseDTO.DangKyTinChiItem> findDangKyTinChiByHocVien(@Param("hocVienId") UUID hocVienId);

    @Query("""
            SELECT lhp.hocKi.id AS hocKiId,
                   lhp.hocKi.maHocKi AS hocKiMa,
                   lhp.hocKi.tenHocKi AS hocKiTen,
                   YEAR(lhp.hocKi.ngayBatDau) AS namHoc,
                   COUNT(DISTINCT hv.id) AS soHocVien,
                   COUNT(d) AS soDangKy,
                   COALESCE(SUM(mh.soTinChi), 0) AS tongTinChi,
                   COALESCE(SUM(mh.soTinChi * 700000.0), 0) AS tongTien
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            GROUP BY lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi, YEAR(lhp.hocKi.ngayBatDau)
            ORDER BY MAX(lhp.hocKi.ngayBatDau) DESC
            """)
    List<HocPhiAdminResponseDTO.DangKyTinChiTheoHocKi> findDangKyTinChiTongHopTheoHocKi();

    @Query("""
            SELECT YEAR(lhp.hocKi.ngayBatDau) AS namHoc,
                   COUNT(DISTINCT lhp.hocKi.id) AS soHocKi,
                   COUNT(DISTINCT hv.id) AS soHocVien,
                   COUNT(d) AS soDangKy,
                   COALESCE(SUM(mh.soTinChi), 0) AS tongTinChi,
                   COALESCE(SUM(mh.soTinChi * 700000.0), 0) AS tongTien
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            GROUP BY YEAR(lhp.hocKi.ngayBatDau)
            ORDER BY namHoc DESC
            """)
    List<HocPhiAdminResponseDTO.DangKyTinChiTheoNamHoc> findDangKyTinChiTongHopTheoNamHoc();

    @Query("""
            SELECT COUNT(DISTINCT hv.id) AS tongSoHocVien,
                   COUNT(d) AS tongSoDangKy,
                   COALESCE(SUM(mh.soTinChi), 0) AS tongSoTinChi,
                   COALESCE(SUM(mh.soTinChi * 700000.0), 0) AS tongSoTien,
                   COUNT(DISTINCT lhp.hocKi.id) AS tongSoHocKi
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            """)
    HocPhiAdminResponseDTO.DangKyTinChiTongQuan findDangKyTinChiTongQuan();

    @Query("""
            SELECT new com.university.dto.response.admin.DangKyTinChiAdminResponseDTO(
                       d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                       lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi, mh.soTinChi * 700000.0,
                       lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                       lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai,
                       COUNT(d2.id), d.createdAt
                   )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users
            LEFT JOIN DangKyTinChi d2 ON d2.lopHocPhan.id = lhp.id
            GROUP BY d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                     lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi,
                     lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                     lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai, d.createdAt
            ORDER BY d.createdAt DESC
            """)
    List<DangKyTinChiAdminResponseDTO> findAllWithDetails();

    @Query("""
            SELECT new com.university.dto.response.admin.DangKyTinChiAdminResponseDTO(
                   d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                   lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi, mh.soTinChi * 700000.0,
                   lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                   lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai,
                   COUNT(d2.id), d.createdAt
                   )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users
            LEFT JOIN DangKyTinChi d2 ON d2.lopHocPhan.id = lhp.id
            WHERE lhp.hocKi.id = :hocKiId
            GROUP BY d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                     lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi,
                     lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                     lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai, d.createdAt
            ORDER BY d.createdAt DESC
            """)
    List<DangKyTinChiAdminResponseDTO> findAllByHocKiIdWithDetails(@Param("hocKiId") UUID hocKiId);

    @Query("""
            SELECT new com.university.dto.response.admin.DangKyTinChiAdminResponseDTO(
                   d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                   lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi, mh.soTinChi * 700000.0,
                   lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                   lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai,
                   COUNT(d2.id), d.createdAt
                   )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users
            LEFT JOIN DangKyTinChi d2 ON d2.lopHocPhan.id = lhp.id
            WHERE lhp.id = :lopHocPhanId
            GROUP BY d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                     lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi,
                     lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                     lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai, d.createdAt
            ORDER BY d.createdAt DESC
            """)
    List<DangKyTinChiAdminResponseDTO> findAllByLopHocPhanIdWithDetails(@Param("lopHocPhanId") UUID lopHocPhanId);

    @Query("""
            SELECT new com.university.dto.response.admin.DangKyTinChiAdminResponseDTO(
                   d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                   lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi, mh.soTinChi * 700000.0,
                   lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                   lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai,
                   COUNT(d2.id), d.createdAt
                   )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users
            LEFT JOIN DangKyTinChi d2 ON d2.lopHocPhan.id = lhp.id
            WHERE hv.id = :hocVienId
            GROUP BY d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                     lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi,
                     lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                     lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai, d.createdAt
            ORDER BY d.createdAt DESC
            """)
    List<DangKyTinChiAdminResponseDTO> findAllByHocVienIdWithDetails(@Param("hocVienId") UUID hocVienId);

    @Query("""
            SELECT new com.university.dto.response.admin.DangKyTinChiAdminResponseDTO(
                   d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                   lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi, mh.soTinChi * 700000.0,
                   lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                   lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai,
                   COUNT(d2.id), d.createdAt
                   )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users
            LEFT JOIN DangKyTinChi d2 ON d2.lopHocPhan.id = lhp.id
            WHERE LOWER(lhp.maLopHocPhan) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(mh.tenMonHoc) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(mh.maMonHoc) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(hv.maHocVien) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(hv.users.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
            GROUP BY d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                     lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi,
                     lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                     lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai, d.createdAt
            ORDER BY d.createdAt DESC
            """)
    List<DangKyTinChiAdminResponseDTO> searchByKeyword(@Param("keyword") String keyword);

    @Query("""
            SELECT lhp.id AS id,
                   lhp.maLopHocPhan AS maLopHocPhan,
                   mh.tenMonHoc AS tenMonHoc,
                   mh.soTinChi AS soTinChi,
                   lhp.hocKi.maHocKi AS hocKiMa,
                   lhp.hocKi.tenHocKi AS hocKiTen,
                   YEAR(lhp.hocKi.ngayBatDau) AS namHoc,
                   CAST(lhp.trangThai AS string) AS trangThai,
                   CAST(COUNT(d.id) AS int) AS soLuongDaDangKy,
                   lhp.soLuongToiDa AS soLuongToiDa,
                   lhp.hanDangKy AS hanDangKy
            FROM LopHocPhan lhp
            JOIN lhp.monHoc mh
            LEFT JOIN lhp.dDangKyTinChis d
            WHERE lhp.trangThai = 'MO_DANG_KY'
            GROUP BY lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi,
                     lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi, YEAR(lhp.hocKi.ngayBatDau),
                     lhp.trangThai, lhp.soLuongToiDa, lhp.hanDangKy
            ORDER BY lhp.hanDangKy DESC
            """)
    List<DangKyTinChiAdminResponseDTO.LopHocPhanDangKyView> findLopHocPhanMoDangKy();

    @Query("""
            SELECT hv.id AS id,
                   hv.maHocVien AS maHocVien,
                   hv.users.hoTen AS hoTen,
                   hv.users.email AS email,
                   hv.users.soDienThoai AS soDienThoai,
                   hv.nganh.tenNganh AS nganhTen
            FROM HocVien hv
            JOIN hv.users
            WHERE hv.ngayTotNghiep IS NULL
            ORDER BY hv.maHocVien ASC
            """)
    List<DangKyTinChiAdminResponseDTO.HocVienDangKyView> findHocVienDangKy();

    @Query("""
            SELECT new com.university.dto.response.admin.DangKyTinChiAdminResponseDTO(
                   d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                   lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi, mh.soTinChi * 700000.0,
                   lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                   lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai,
                   COUNT(d2.id), d.createdAt
                   )
            FROM DangKyTinChi d
            JOIN d.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN d.hocVien hv
            JOIN hv.users
            LEFT JOIN DangKyTinChi d2 ON d2.lopHocPhan.id = lhp.id
            WHERE d.id = :id
            GROUP BY d.id, hv.id, hv.maHocVien, hv.users.hoTen, hv.users.email, hv.users.soDienThoai,
                     lhp.id, lhp.maLopHocPhan, mh.tenMonHoc, mh.soTinChi,
                     lhp.hocKi.id, lhp.hocKi.maHocKi, lhp.hocKi.tenHocKi,
                     lhp.hanDangKy, lhp.hanHuy, lhp.soLuongToiDa, lhp.trangThai, d.createdAt
            """)
    Optional<DangKyTinChiAdminResponseDTO> findByIdWithDetails(@Param("id") UUID id);
}
