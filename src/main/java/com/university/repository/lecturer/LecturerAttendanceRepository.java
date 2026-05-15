package com.university.repository.lecturer;

import com.university.entity.DiemDanh;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerAttendanceRepository extends JpaRepository<DiemDanh, UUID> {
    List<DiemDanh> findByLich_LopHocPhan_Id(UUID lopHocPhanId);

    List<DiemDanh> findByLich_Id(UUID lichId);

    @Query("SELECT dd FROM DiemDanh dd JOIN dd.lich l JOIN l.lopHocPhan lhp JOIN lhp.dGiangDays gd JOIN gd.nhanVien nv JOIN nv.users u WHERE u.id = :userId")
    List<DiemDanh> findByLich_LopHocPhan_DGiangDays_NhanVien_Users_Id(@Param("userId") UUID userId);

    Optional<DiemDanh> findByHocVien_IdAndLich_Id(UUID hocVienId, UUID lichId);
}
