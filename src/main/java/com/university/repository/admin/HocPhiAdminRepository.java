package com.university.repository.admin;

import com.university.dto.response.admin.HocPhiAdminResponseDTO;
import com.university.entity.HocPhi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HocPhiAdminRepository extends JpaRepository<HocPhi, UUID> {

        List<HocPhiAdminResponseDTO.HocPhiView> findAllProjectedBy();

        List<HocPhi> findAllByHocKiId(UUID hocKiId);

        boolean existsByHocVienId(UUID hocVienId);

        @Query("""
                        SELECT SUM(mh.soTinChi)
                        FROM DangKyTinChi d
                        JOIN d.lopHocPhan lhp
                        JOIN lhp.monHoc mh
                        WHERE d.hocVien.id = :userId
                        """)
        Long getTongTinChiByHocVien(@Param("userId") UUID userId);

        @Query("""
                        SELECT SUM(mh.soTinChi)
                        FROM DangKyTinChi d
                        JOIN d.lopHocPhan lhp
                        JOIN lhp.monHoc mh
                        WHERE d.hocVien.id = :hocVienId
                        AND lhp.hocKi.id = :hocKiId
                        """)
        Long getTongTinChiByHocVienAndHocKi(
                        @Param("hocVienId") UUID hocVienId,
                        @Param("hocKiId") UUID hocKiId);

        @Query("SELECT COUNT(h) FROM HocPhi h")
        Long getTongSoHocPhi();

        @Query("SELECT COALESCE(SUM(h.soTien), 0) FROM HocPhi h")
        Double getTongSoTien();

        @Query("SELECT COUNT(h) FROM HocPhi h WHERE h.trangThai = 'CHUA_THANH_TOAN'")
        Long getSoChuaThanhToan();

        @Query("SELECT COUNT(h) FROM HocPhi h WHERE h.trangThai = 'DA_THANH_TOAN'")
        Long getSoDaThanhToan();

        @Query("SELECT COUNT(h) FROM HocPhi h WHERE h.trangThai = 'QUA_HAN'")
        Long getSoQuaHan();

        @Query("""
                        SELECT h.hocKi.id AS hocKiId,
                               h.hocKi.maHocKi AS hocKiMa,
                               h.hocKi.tenHocKi AS hocKiTen,
                               COUNT(h) AS soLuong,
                               COALESCE(SUM(h.soTien), 0) AS tongTien,
                               COALESCE(SUM(CASE WHEN h.trangThai = 'DA_THANH_TOAN' THEN h.soTien ELSE 0 END), 0) AS tienDaThu,
                               COALESCE(SUM(CASE WHEN h.trangThai != 'DA_THANH_TOAN' THEN h.soTien ELSE 0 END), 0) AS tienConNo,
                               COUNT(CASE WHEN h.trangThai = 'CHUA_THANH_TOAN' THEN 1 END) AS soChuaThanhToan,
                               COUNT(CASE WHEN h.trangThai = 'DA_THANH_TOAN' THEN 1 END) AS soDaThanhToan
                        FROM HocPhi h
                        GROUP BY h.hocKi.id, h.hocKi.maHocKi, h.hocKi.tenHocKi, h.hocKi.ngayBatDau
                        ORDER BY h.hocKi.ngayBatDau DESC
                        """)
        List<HocPhiAdminResponseDTO.DashboardTheoHocKi> getDashboardTheoHocKi();

        @Query("""
                        SELECT YEAR(t.ngayThanhToan) AS nam,
                               MONTH(t.ngayThanhToan) AS thang,
                               COUNT(h) AS soLuong,
                               COALESCE(SUM(h.soTien), 0) AS tongTien,
                               COALESCE(SUM(CASE WHEN h.trangThai = 'DA_THANH_TOAN' THEN h.soTien ELSE 0 END), 0) AS tienDaThu
                        FROM HocPhi h
                        LEFT JOIN h.thanhToanHocPhi t
                        GROUP BY YEAR(t.ngayThanhToan), MONTH(t.ngayThanhToan)
                        ORDER BY nam DESC, thang DESC
                        """)
        List<HocPhiAdminResponseDTO.DashboardTheoThang> getDashboardTheoThang();

        @Query("""
                        SELECT h.hocVien.id AS hocVienId,
                               h.hocVien.users.hoTen AS hoTen,
                               h.hocVien.maHocVien AS maHocVien,
                               COALESCE(SUM(h.soTien), 0) AS soTienNo,
                               COUNT(h) AS soLanNo
                        FROM HocPhi h
                        WHERE h.trangThai != 'DA_THANH_TOAN'
                        GROUP BY h.hocVien.id, h.hocVien.users.hoTen, h.hocVien.maHocVien
                        ORDER BY soTienNo DESC
                        """)
        List<HocPhiAdminResponseDTO.DashboardTopNo> getDashboardTopNo();

        @Query("""
                        SELECT COUNT(h) AS tongSoHocPhi,
                               COALESCE(SUM(h.soTien), 0) AS tongSoTien,
                               COUNT(CASE WHEN h.trangThai = 'CHUA_THANH_TOAN' THEN 1 END) AS soChuaThanhToan,
                               COUNT(CASE WHEN h.trangThai = 'DA_THANH_TOAN' THEN 1 END) AS soDaThanhToan,
                               COUNT(CASE WHEN h.trangThai = 'QUA_HAN' THEN 1 END) AS soQuaHan
                        FROM HocPhi h
                        """)
        HocPhiAdminResponseDTO.DashboardTongQuan getDashboardTongQuan();

        @Query("""
                        SELECT hp FROM HocPhi hp
                        WHERE (:start IS NULL OR hp.createdAt >= :start)
                          AND (:end IS NULL OR hp.createdAt <= :end)
                        """)
        List<HocPhi> findByCreatedAtRange(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        void deleteAllByIdIn(List<UUID> ids);

        @Query("SELECT hp FROM HocPhi hp WHERE hp.hocVien.id = :hocVienId AND hp.hocKi.id = :hocKiId")
        Optional<HocPhi> findByHocVienIdAndHocKiId(
                        @Param("hocVienId") UUID hocVienId,
                        @Param("hocKiId") UUID hocKiId);

        @Query("SELECT hp FROM HocPhi hp WHERE hp.hocVien.id = :hocVienId AND hp.hocKi.id = :hocKiId")
        List<HocPhi> findAllByHocVienIdAndHocKiId(
                        @Param("hocVienId") UUID hocVienId,
                        @Param("hocKiId") UUID hocKiId);

        List<HocPhi> findAllByHocVienId(UUID hocVienId);
}
