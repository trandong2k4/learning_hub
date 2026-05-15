package com.university.repository.lecturer;

import com.university.entity.LichSuDiem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerLichSuDiemRepository extends JpaRepository<LichSuDiem, UUID> {

    @Query("""
        SELECT ls FROM LichSuDiem ls
        JOIN ls.diemThanhPhan dtp
        JOIN dtp.dangKyTinChi dkt
        JOIN dkt.hocVien hv
        JOIN dkt.lopHocPhan lhp
        JOIN lhp.dGiangDays gd
        JOIN gd.nhanVien nv
        JOIN nv.users u
        WHERE u.id = :userId
        AND hv.id = :hocVienId
        AND lhp.id = :lopHocPhanId
        ORDER BY ls.thoiGianThayDoi DESC
    """)
    List<LichSuDiem> findByStudentAndClass(
            @Param("userId") UUID userId,
            @Param("hocVienId") UUID hocVienId,
            @Param("lopHocPhanId") UUID lopHocPhanId);

    List<LichSuDiem> findByDiemThanhPhan_IdOrderByThoiGianThayDoiDesc(UUID diemThanhPhanId);
}
