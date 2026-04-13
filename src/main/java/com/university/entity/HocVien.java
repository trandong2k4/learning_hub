package com.university.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hoc_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocVien {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 10, unique = true, nullable = false)
    private String maHocVien;

    private LocalDateTime ngayNhapHoc;

    private LocalDateTime ngayTotNghiep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nganh_id", nullable = false)
    private Nganh nganh;

    @OneToMany(mappedBy = "hocVien", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiemDanh> dDiemDanhs = new ArrayList<>();

    @OneToMany(mappedBy = "hocVien", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DangKyTinChi> dDangKyTinChis = new ArrayList<>();

    @OneToMany(mappedBy = "hocVien", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HocPhi> dHocPhis = new ArrayList<>();

    @OneToMany(mappedBy = "hocVien", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAttempt> dQuizAttempts = new ArrayList<>();

    @OneToMany(mappedBy = "hocVien", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmitExercise> dSubmitExercises = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false, unique = true)
    private Users users;
}