package com.university.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attempt_answers_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAnswersLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questions_id", nullable = false)
    private Questions questions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_answers_id")
    private Answers oldAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_answers_id")
    private Answers newAnswer;

    @Column(columnDefinition = "TEXT")
    private String oldTextAnswer;

    @Column(columnDefinition = "TEXT")
    private String newTextAnswer;

    private Integer timeOnQuestion;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime changedAt;
}
