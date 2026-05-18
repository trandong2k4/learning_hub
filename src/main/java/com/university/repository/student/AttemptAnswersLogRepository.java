package com.university.repository.student;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.entity.AttemptAnswersLog;

@Repository
public interface AttemptAnswersLogRepository extends JpaRepository<AttemptAnswersLog, UUID> {
    List<AttemptAnswersLog> findByQuizAttempt_IdOrderByChangedAtAsc(UUID attemptId);
}
