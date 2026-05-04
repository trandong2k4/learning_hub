package com.university.repository.student;

import com.university.entity.DangKyTinChi;
import com.university.entity.DiemThanhPhan;
import com.university.entity.ChuongTrinhDaoTao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TienDoHocTapStudentRepository extends JpaRepository<DangKyTinChi, UUID> {

    @Query("""
        SELECT ct
        FROM ChuongTrinhDaoTao ct
        JOIN FETCH ct.monHoc mh
        WHERE ct.nganh.id = :nganhId
        ORDER BY mh.maMonHoc ASC
    """)
    List<ChuongTrinhDaoTao> findCurriculumByNganhId(@Param("nganhId") UUID nganhId);

    @Query("""
        SELECT d
        FROM DangKyTinChi d
        JOIN FETCH d.lopHocPhan lhp
        JOIN FETCH lhp.monHoc mh
        JOIN FETCH lhp.hocKi hk
        WHERE d.hocVien.id = :hocVienId
        ORDER BY hk.ngayBatDau ASC, hk.maHocKi ASC, mh.maMonHoc ASC
    """)
    List<DangKyTinChi> findTranscriptByHocVienId(@Param("hocVienId") UUID hocVienId);

    @Query("""
        SELECT dtp
        FROM DiemThanhPhan dtp
        JOIN FETCH dtp.cotDiem cd
        WHERE dtp.dangKyTinChi.id IN :dangKyIds
        ORDER BY dtp.dangKyTinChi.id ASC, cd.thuTuHienThi ASC, dtp.lanNhap DESC, dtp.updatedAt DESC
    """)
    List<DiemThanhPhan> findGradesByDangKyIds(@Param("dangKyIds") List<UUID> dangKyIds);
}
