package com.university.repository.student;


import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.university.entity.Questions;
@Repository
public interface QuestionsRepository extends JpaRepository<Questions, UUID> {
    List<Questions> findByExercise_Id(UUID exerciseId);

    List<Questions> findByExercise_LopHocPhan_Id(UUID lopHocPhanId);

    List<Questions> findByExercise_LopHocPhan_IdAndLoaiCauHoiIn(UUID lopHocPhanId, List<Boolean> loaiCauHoi);

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
}
