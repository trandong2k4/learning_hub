package com.university.repository.student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import com.university.entity.QuizAttempt;
import java.util.List;
import java.util.Optional;
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {

     // Thêm mới: Kiểm tra đã nộp bài chưa (status = true)
    boolean existsByQuiz_IdAndHocVien_IdAndStatusTrue(UUID quizId, UUID hocVienId);

    // Thêm mới: Lấy attempt đang làm dở (status = false)
    Optional<QuizAttempt> findByQuiz_IdAndHocVien_IdAndStatusFalse(UUID quizId, UUID hocVienId);

    List<QuizAttempt> findByHocVien_IdAndQuiz_IdIn(UUID hocVienId, List<UUID> quizIds);

    @Query("""
            SELECT qa
            FROM QuizAttempt qa
            JOIN FETCH qa.quiz
            WHERE qa.hocVien.id = :hocVienId
            AND qa.quiz.id IN :quizIds
            """)
    List<QuizAttempt> findByHocVienIdAndQuizIdsWithQuiz(
            @Param("hocVienId") UUID hocVienId,
            @Param("quizIds") List<UUID> quizIds);

    Optional<QuizAttempt> findTopByQuiz_IdAndHocVien_IdOrderByStartTimeDesc(UUID quizId, UUID hocVienId);

    long countByQuiz_IdAndHocVien_Id(UUID quizId, UUID hocVienId);

    @Query("""
            SELECT qa
            FROM QuizAttempt qa
            JOIN FETCH qa.quiz
            JOIN FETCH qa.hocVien
            WHERE qa.id = :attemptId
            """)
    Optional<QuizAttempt> findByIdWithQuizAndHocVien(@Param("attemptId") UUID attemptId);

    @Query("""
            SELECT qa
            FROM QuizAttempt qa
            JOIN FETCH qa.quiz
            JOIN FETCH qa.hocVien
            WHERE qa.quiz.id = :quizId
            AND qa.hocVien.id = :hocVienId
            AND qa.status = false
            """)
    Optional<QuizAttempt> findOpenByQuizIdAndHocVienIdWithQuizAndHocVien(
            @Param("quizId") UUID quizId,
            @Param("hocVienId") UUID hocVienId);
    
}

