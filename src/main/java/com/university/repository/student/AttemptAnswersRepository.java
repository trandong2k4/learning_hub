package com.university.repository.student;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.university.entity.AttemptAnswers;

public interface AttemptAnswersRepository extends JpaRepository<AttemptAnswers, UUID> {
    void deleteByQuizAttempt_Id(UUID quizAttemptId);

    List<AttemptAnswers> findByQuizAttempt_Id(UUID quizAttemptId);

    Optional<AttemptAnswers> findByQuizAttempt_IdAndQuestions_Id(UUID quizAttemptId, UUID questionId);

    @Query("""
            SELECT DISTINCT aa
            FROM AttemptAnswers aa
            JOIN FETCH aa.questions q
            LEFT JOIN FETCH q.dAnswers
            LEFT JOIN FETCH aa.answers
            WHERE aa.quizAttempt.id = :quizAttemptId
            """)
    List<AttemptAnswers> findByQuizAttemptIdWithQuestionAnswerData(@Param("quizAttemptId") UUID quizAttemptId);

    @Query("""
            SELECT DISTINCT aa
            FROM AttemptAnswers aa
            JOIN FETCH aa.questions q
            LEFT JOIN FETCH aa.answers
            WHERE aa.quizAttempt.id = :quizAttemptId
            AND q.id IN :questionIds
            """)
    List<AttemptAnswers> findByQuizAttemptIdAndQuestionIdsWithAnswer(
            @Param("quizAttemptId") UUID quizAttemptId,
            @Param("questionIds") List<UUID> questionIds);
}
