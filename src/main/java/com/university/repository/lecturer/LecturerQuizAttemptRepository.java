package com.university.repository.lecturer;

import com.university.entity.QuizAttempt;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerQuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    @Query("SELECT qa FROM QuizAttempt qa JOIN qa.quiz q JOIN q.lopHocPhan lhp JOIN lhp.dGiangDays gd JOIN gd.nhanVien nv JOIN nv.users u WHERE u.id = :userId")
    List<QuizAttempt> findByQuiz_LopHocPhan_DGiangDays_NhanVien_Users_Id(@Param("userId") UUID userId);

    List<QuizAttempt> findByQuiz_Id(UUID quizId);
    List<QuizAttempt> findByHocVien_Id(UUID hocVienId);

    @Query("""
            SELECT COUNT(qa) FROM QuizAttempt qa
            JOIN qa.quiz q
            JOIN q.lopHocPhan lhp
            JOIN lhp.dGiangDays gd
            JOIN gd.nhanVien nv
            JOIN nv.users u
            WHERE u.id = :userId
              AND qa.status = true
            """)
    int countCompletedAttemptsByLecturer(@Param("userId") UUID userId);

    @Query("""
            SELECT qa FROM QuizAttempt qa
            JOIN FETCH qa.quiz q
            JOIN FETCH q.lopHocPhan lhp
            JOIN FETCH lhp.monHoc
            JOIN FETCH qa.hocVien hv
            JOIN FETCH hv.users
            JOIN lhp.dGiangDays gd
            JOIN gd.nhanVien nv
            JOIN nv.users u
            WHERE u.id = :userId
              AND qa.status = true
            ORDER BY qa.endTime DESC, qa.startTime DESC
            """)
    List<QuizAttempt> findCompletedAttemptsByLecturer(@Param("userId") UUID userId, Pageable pageable);
}
