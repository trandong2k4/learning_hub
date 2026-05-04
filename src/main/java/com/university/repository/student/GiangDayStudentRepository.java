package com.university.repository.student;

import com.university.entity.GiangDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GiangDayStudentRepository extends JpaRepository<GiangDay, UUID> {

    @Query("""
        SELECT gd
        FROM GiangDay gd
        JOIN FETCH gd.nhanVien nv
        JOIN FETCH nv.users u
        JOIN FETCH gd.lopHocPhan lhp
        WHERE lhp.id IN :lopHocPhanIds
    """)
    List<GiangDay> findByLopHocPhanIds(@Param("lopHocPhanIds") List<UUID> lopHocPhanIds);
}
