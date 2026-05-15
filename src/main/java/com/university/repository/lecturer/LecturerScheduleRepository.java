package com.university.repository.lecturer;

import com.university.entity.Lich;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerScheduleRepository extends JpaRepository<Lich, UUID> {
    @Query("SELECT l FROM Lich l JOIN FETCH l.lopHocPhan lp JOIN FETCH lp.dGiangDays gd JOIN FETCH gd.nhanVien nv JOIN FETCH nv.users u WHERE u.id = :userId AND l.ngayHoc BETWEEN :start AND :end")
    List<Lich> findByLopHocPhan_DGiangDays_NhanVien_Users_IdAndNgayHocBetween(@Param("userId") UUID userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT l FROM Lich l JOIN FETCH l.lopHocPhan lp JOIN FETCH lp.dGiangDays gd JOIN FETCH gd.nhanVien nv JOIN FETCH nv.users u WHERE u.id = :userId")
    List<Lich> findByLopHocPhan_DGiangDays_NhanVien_Users_Id(@Param("userId") UUID userId);
}
