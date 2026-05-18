package com.university.repository.lecturer;

import com.university.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LecturerExerciseRepository extends JpaRepository<Exercise, UUID> {
    List<Exercise> findByLopHocPhan_Id(UUID lopHocPhanId);

    @Query("""
            SELECT e
            FROM Exercise e
            JOIN FETCH e.lopHocPhan
            WHERE e.id IN :ids
            """)
    List<Exercise> findAllByIdInWithClass(@Param("ids") List<UUID> ids);

    @Query("SELECT e FROM Exercise e JOIN e.lopHocPhan lhp JOIN lhp.dGiangDays gd JOIN gd.nhanVien nv JOIN nv.users u WHERE u.id = :userId")
    List<Exercise> findByLopHocPhan_DGiangDays_NhanVien_Users_Id(@Param("userId") UUID userId);
}
