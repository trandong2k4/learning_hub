package com.university.repository.student;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.entity.QuizAttemptLog;

@Repository
public interface QuizAttemptLogRepository extends JpaRepository<QuizAttemptLog, UUID> {
}
