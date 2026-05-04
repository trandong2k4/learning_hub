package com.university.repository.student;

import com.university.entity.Lich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LichStudentRepository extends JpaRepository<Lich, UUID> {

    @Query("""
        SELECT DISTINCT lich
        FROM DangKyTinChi d
        JOIN d.lopHocPhan lhp
        JOIN lhp.dLichs lich
        JOIN FETCH lich.gioHoc gh
        JOIN FETCH lich.phong p
        JOIN FETCH lich.lopHocPhan lhpFetch
        JOIN FETCH lhpFetch.monHoc mh
        WHERE d.hocVien.id = :hocVienId
        AND lich.ngayHoc >= :startDateTime
        AND lich.ngayHoc < :endDateTime
        ORDER BY lich.ngayHoc ASC, gh.thoiGianBatDau ASC, gh.thoiGianKetThuc ASC
    """)
    List<Lich> findPersonalSchedule(
            @Param("hocVienId") UUID hocVienId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
