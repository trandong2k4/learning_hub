package com.university.repository.student;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.university.entity.AttemptAnswers;

public interface AttemptAnswersRepository extends JpaRepository<AttemptAnswers, UUID> {
    void deleteByQuizAttempt_Id(UUID quizAttemptId);
}
