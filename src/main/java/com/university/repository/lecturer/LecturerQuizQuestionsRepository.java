package com.university.repository.lecturer;

import com.university.entity.QuizQuestions;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerQuizQuestionsRepository extends JpaRepository<QuizQuestions, UUID> {
    List<QuizQuestions> findByQuiz_Id(UUID quizId);
}
