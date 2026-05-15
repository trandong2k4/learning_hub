package com.university.repository.admin;

import com.university.entity.SubmitExercise;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminSubmissionRepository extends JpaRepository<SubmitExercise, UUID> {

    @EntityGraph(attributePaths = {
            "exercise",
            "exercise.lopHocPhan",
            "exercise.lopHocPhan.monHoc",
            "hocVien",
            "hocVien.users"
    })
    @Query("""
            SELECT s FROM SubmitExercise s
            JOIN s.exercise e
            JOIN e.lopHocPhan lhp
            JOIN lhp.monHoc mh
            JOIN s.hocVien hv
            JOIN hv.users u
            WHERE (:lopHocPhanId IS NULL OR lhp.id = :lopHocPhanId)
              AND (
                    :keyword IS NULL OR :keyword = ''
                    OR LOWER(e.tieuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(hv.maHocVien) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(lhp.maLopHocPhan) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<SubmitExercise> searchSubmissions(
            @Param("keyword") String keyword,
            @Param("lopHocPhanId") UUID lopHocPhanId,
            Pageable pageable);
}
