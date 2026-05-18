package com.university.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.university.enums.AttemptActionEnum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_attempt_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private AttemptActionEnum action;

    @Column(columnDefinition = "TEXT")
    private String value; // key dap an

    @Column(columnDefinition = "TEXT")
    private String eventData;

    private String ipAddress;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questions_id", nullable = true)
    private Questions questions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;

}
