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

    @Query("""
            SELECT q
            FROM Quiz q
            JOIN FETCH q.lopHocPhan lhp
            JOIN FETCH lhp.monHoc
            WHERE q.id = :quizId
            """)
    java.util.Optional<Quiz> findByIdWithClassAndSubject(@Param("quizId") UUID quizId);

    @Query("""
            SELECT q
            FROM Quiz q
            JOIN FETCH q.lopHocPhan lhp
            JOIN FETCH lhp.monHoc
            WHERE lhp.id = :lopHocPhanId
            ORDER BY q.createdAt DESC
            """)
    List<Quiz> findByLopHocPhanIdWithClassAndSubject(@Param("lopHocPhanId") UUID lopHocPhanId);

    @Query("""
            SELECT qq.quiz.id, COUNT(qq.id)
            FROM QuizQuestions qq
            WHERE qq.quiz.id IN :quizIds
            GROUP BY qq.quiz.id
            """)
    List<Object[]> countManualQuestionsByQuizIds(@Param("quizIds") List<UUID> quizIds);

    @Query("""
            SELECT qe.quiz.id, COUNT(DISTINCT q.id)
            FROM QuizExercise qe
            JOIN qe.exercise exercise
            JOIN exercise.dQuestions q
            WHERE qe.quiz.id IN :quizIds
            GROUP BY qe.quiz.id
            """)
    List<Object[]> countExerciseQuestionsByQuizIds(@Param("quizIds") List<UUID> quizIds);

    boolean existsByLopHocPhan_IdAndTieuDeAndThoiGianBatDauAndThoiGianKetThuc(
            UUID lopHocPhanId,
            String tieuDe,
            java.time.LocalDateTime thoiGianBatDau,
            java.time.LocalDateTime thoiGianKetThuc);

    @Query("SELECT q FROM Quiz q JOIN q.lopHocPhan lhp JOIN lhp.dGiangDays gd JOIN gd.nhanVien nv JOIN nv.users u WHERE u.id = :userId")
    List<Quiz> findByLopHocPhan_DGiangDays_NhanVien_Users_Id(@Param("userId") UUID userId);
}
