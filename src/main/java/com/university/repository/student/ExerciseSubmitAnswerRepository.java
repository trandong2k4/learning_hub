package com.university.repository.student;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.entity.ExerciseSubmitAnswer;

@Repository
public interface ExerciseSubmitAnswerRepository extends JpaRepository<ExerciseSubmitAnswer, UUID> {

    List<ExerciseSubmitAnswer> findBySubmitExercise_Id(UUID submitExerciseId);

    @Query("SELECT esa FROM ExerciseSubmitAnswer esa WHERE esa.submitExercise.exercise.id = :exerciseId AND esa.submitExercise.hocVien.id = :hocVienId")
    List<ExerciseSubmitAnswer> findByExerciseIdAndHocVienId(
            @Param("exerciseId") UUID exerciseId,
            @Param("hocVienId") UUID hocVienId);

    @Query("""
            SELECT esa FROM ExerciseSubmitAnswer esa
            JOIN FETCH esa.questions q
            LEFT JOIN FETCH q.dAnswers
            JOIN FETCH esa.submitExercise se
            JOIN FETCH se.hocVien hv
            WHERE se.id = :submissionId
            """)
    List<ExerciseSubmitAnswer> findBySubmissionIdWithQuestions(@Param("submissionId") UUID submissionId);
}
