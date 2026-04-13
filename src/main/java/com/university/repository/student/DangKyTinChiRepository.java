package com.university.repository.student;

import java.util.List;
import java.util.UUID;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.university.entity.DangKyTinChi;
import com.university.dto.response.student.DangKyTinChiResponseDTO;

public interface DangKyTinChiRepository extends JpaRepository<DangKyTinChi, UUID> {

    // ========================
    // 1. CHECK ĐÃ ĐĂNG KÝ
    // ========================
    boolean existsByHocVienIdAndLopHocPhanId(UUID hocVienId, UUID lopHocPhanId);

    // ========================
    // 2. COUNT SỐ LƯỢNG TRONG LỚP
    // ========================
    int countByLopHocPhanId(UUID lopHocPhanId);

    // ========================
    // 3. TỔNG TÍN CHỈ
    // ========================
    @Query("""
        SELECT SUM(lhp.monHoc.soTinChi)
        FROM DangKyTinChi d
        JOIN d.lopHocPhan lhp
        WHERE d.hocVien.id = :hocVienId
    """)
    Integer sumTinChiByHocVien(@Param("hocVienId") UUID hocVienId);

    // ========================
    // 4. CHECK ĐÃ HỌC MÔN
    // ========================
    @Query("""
        SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
        FROM DangKyTinChi d
        WHERE d.hocVien.id = :hocVienId
        AND d.lopHocPhan.monHoc.id = :monHocId
    """)
    boolean daHocMon(@Param("hocVienId") UUID hocVienId,
                     @Param("monHocId") UUID monHocId);

    // ========================
    // 5. CHECK MÔN TIÊN QUYẾT
    // ========================
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
    boolean daHocMonTienQuyet(@Param("hocVienId") UUID hocVienId,
                             @Param("monHocId") UUID monHocId);

    // ========================
    // 6. CHECK TRÙNG LỊCH (PRO - KHÔNG LOOP)
    // ========================
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
    boolean existsTrungLichFull(@Param("hocVienId") UUID hocVienId,
                               @Param("lopHocPhanId") UUID lopHocPhanId);

    // ========================
    // 7. LẤY DANH SÁCH DTO
    // ========================
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
    List<DangKyTinChiResponseDTO> findDangKyTinChiResponseDTOByHocVienId(
            @Param("hocVienId") UUID hocVienId
    );

    // ========================
    // 8. LẤY DTO THEO ID
    // ========================
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
    java.util.Optional<DangKyTinChiResponseDTO> findDangKyTinChiResponseDTOById(
            @Param("id") UUID id
    );
}