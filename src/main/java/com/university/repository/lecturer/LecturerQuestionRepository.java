package com.university.repository.lecturer;

import com.university.entity.Questions;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerQuestionRepository extends JpaRepository<Questions, UUID> {
    List<Questions> findByExercise_Id(UUID exerciseId);

    @Query("""
            SELECT DISTINCT q
            FROM Questions q
            LEFT JOIN FETCH q.dAnswers
            WHERE q.exercise.id IN :exerciseIds
            """)
    List<Questions> findByExerciseIdsWithAnswers(@Param("exerciseIds") List<UUID> exerciseIds);

    @Query("""
            SELECT DISTINCT q
            FROM Quiz quiz
            JOIN quiz.dQuizQuestions qq
            JOIN qq.questions q
            LEFT JOIN FETCH q.dAnswers
            WHERE quiz.id = :quizId
            """)
    List<Questions> findManualQuizQuestionsWithAnswers(@Param("quizId") UUID quizId);

    @Query("""
            SELECT DISTINCT q
            FROM Quiz quiz
            JOIN quiz.dQuizExercises qe
            JOIN qe.exercise exercise
            JOIN exercise.dQuestions q
            LEFT JOIN FETCH q.dAnswers
            WHERE quiz.id = :quizId
            """)
    List<Questions> findExerciseQuizQuestionsWithAnswers(@Param("quizId") UUID quizId);

    @Query("SELECT q FROM Questions q JOIN q.exercise e JOIN e.lopHocPhan lhp JOIN lhp.dGiangDays gd JOIN gd.nhanVien nv JOIN nv.users u WHERE u.id = :userId")
    List<Questions> findByExercise_LopHocPhan_DGiangDays_NhanVien_Users_Id(@Param("userId") UUID userId);

    void deleteByExercise_Id(UUID exerciseId);
}
