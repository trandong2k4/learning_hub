package com.university.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "submit_exercise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer phienThucHien;

    private String fileExerciseUrl;

    private LocalDateTime thoiGianNop;

    @Column
    private Double diem;

    private String ghiChu;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_vien_id", nullable = false)
    private HocVien hocVien;

    @OneToMany(mappedBy = "submitExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseSubmitAnswer> dExerciseSubmitAnswers = new ArrayList<>();
}
