package com.university.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Answers {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 1)
    private String keyAnswers;

    @Column(nullable = false)
    private String conText;

    private Boolean isCorrect;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questions_id", nullable = false)
    private Questions questions;
    @OneToMany(mappedBy = "answers", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttemptAnswers> dAttemptAnswers = new ArrayList<>();

}
