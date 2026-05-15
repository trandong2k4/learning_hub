package com.university.repository.lecturer;

import com.university.entity.Quiz;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerQuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findByLopHocPhan_Id(UUID lopHocPhanId);

    @Query("SELECT q FROM Quiz q JOIN q.lopHocPhan lhp JOIN lhp.dGiangDays gd JOIN gd.nhanVien nv JOIN nv.users u WHERE u.id = :userId")
    List<Quiz> findByLopHocPhan_DGiangDays_NhanVien_Users_Id(@Param("userId") UUID userId);
}
