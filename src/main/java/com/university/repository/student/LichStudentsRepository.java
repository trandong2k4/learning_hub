package com.university.repository.student;

import com.university.entity.Lich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LichStudentsRepository extends JpaRepository<Lich, UUID> {

    @Query("""
            SELECT l FROM Lich l
            JOIN FETCH l.gioHoc gh
            JOIN FETCH l.phong p
            WHERE l.lopHocPhan.id = :lopHocPhanId
            ORDER BY l.ngayHoc ASC, gh.thoiGianBatDau ASC
            """)
    List<Lich> findByLopHocPhanId(@Param("lopHocPhanId") UUID lopHocPhanId);
}
