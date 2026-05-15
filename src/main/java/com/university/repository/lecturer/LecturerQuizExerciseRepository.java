package com.university.repository.lecturer;

import com.university.entity.QuizExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LecturerQuizExerciseRepository extends JpaRepository<QuizExercise, UUID> {
    List<QuizExercise> findByQuiz_Id(UUID quizId);
    void deleteByQuiz_Id(UUID quizId);
}