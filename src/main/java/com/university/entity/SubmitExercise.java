package com.university.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submit_exercise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer phienThucHien;

    @Column(nullable = false)
    private String fileExerciseUrl;

    private LocalDateTime thoiGianNop;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_vien_id", nullable = false)
    private HocVien hocVien;
}
