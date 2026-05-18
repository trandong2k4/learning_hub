package com.university.entity;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attempt_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAnswers {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime answeredAt;

    @Column(columnDefinition = "TEXT")
    private String textAnswer;

    private Boolean isCorrect;

    @Column(precision = 5, scale = 2)
    private BigDecimal scoreReceived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questions_id", nullable = false)
    private Questions questions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answers_id", nullable = true)
    private Answers answers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;

}
