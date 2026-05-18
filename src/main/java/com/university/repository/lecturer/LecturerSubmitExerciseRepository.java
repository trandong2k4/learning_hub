package com.university.repository.lecturer;

import com.university.entity.SubmitExercise;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerSubmitExerciseRepository extends JpaRepository<SubmitExercise, UUID> {
    List<SubmitExercise> findByExercise_LopHocPhan_Id(UUID lopHocPhanId);

    List<SubmitExercise> findByExercise_Id(UUID exerciseId);

    int countByExercise_Id(UUID exerciseId);

    @Query("SELECT se.exercise.id, COUNT(se) FROM SubmitExercise se WHERE se.exercise.id IN :exerciseIds GROUP BY se.exercise.id")
    List<Object[]> countByExercise_IdIn(@Param("exerciseIds") List<UUID> exerciseIds);

    @Query("SELECT COUNT(se) FROM SubmitExercise se JOIN se.exercise e JOIN e.lopHocPhan lhp JOIN lhp.dGiangDays gd JOIN gd.nhanVien nv JOIN nv.users u WHERE u.id = :userId")
    int countByExercise_LopHocPhan_DGiangDays_NhanVien_Users_Id(@Param("userId") UUID userId);

    @Query("SELECT COUNT(se) FROM SubmitExercise se JOIN se.exercise e JOIN e.lopHocPhan lhp JOIN lhp.dGiangDays gd JOIN gd.nhanVien nv JOIN nv.users u WHERE u.id = :userId AND se.diem IS NULL")
    int countUngradedSubmissionsByLecturer(@Param("userId") UUID userId);

    @Query("""
            SELECT se FROM SubmitExercise se
            JOIN FETCH se.exercise e
            JOIN FETCH se.hocVien hv
            JOIN FETCH hv.users
            WHERE se.exercise.id = :exerciseId
            """)
    List<SubmitExercise> findByExercise_IdWithHocVien(@Param("exerciseId") UUID exerciseId);

    @Query("""
            SELECT se FROM SubmitExercise se
            JOIN FETCH se.exercise e
            JOIN FETCH e.lopHocPhan lhp
            JOIN FETCH lhp.monHoc
            JOIN FETCH se.hocVien hv
            JOIN FETCH hv.users
            JOIN lhp.dGiangDays gd
            JOIN gd.nhanVien nv
            JOIN nv.users u
            WHERE u.id = :userId
              AND se.diem IS NULL
            ORDER BY COALESCE(se.thoiGianNop, se.createdAt) DESC
            """)
    List<SubmitExercise> findUngradedSubmissionsByLecturer(@Param("userId") UUID userId, Pageable pageable);
}
