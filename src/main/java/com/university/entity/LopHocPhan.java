package com.university.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import com.university.enums.TrangThaiLHP;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lop_hoc_phan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LopHocPhan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 10, unique = true, nullable = false)
    private String maLopHocPhan;

    private Integer soLuongToiDa;

    @Enumerated(EnumType.STRING)
    private TrangThaiLHP trangThai;

    // ✅ THÊM MỚI
    @Column(nullable = false)
    private LocalDateTime hanDangKy;

    @Column(nullable = false)
    private LocalDateTime hanHuy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_ki_id", nullable = false)
    private HocKi hocKi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mon_hoc_id", nullable = false)
    private MonHoc monHoc;

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lich> dLichs = new ArrayList<>();

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GiangDay> dGiangDays = new ArrayList<>();

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DanhGiaGiangVien> dDanhGiaGiangViens = new ArrayList<>();

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DangKyTinChi> dDangKyTinChis = new ArrayList<>();

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaiLieu> dTaiLieus = new ArrayList<>();

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CotDiem> dCotDiems = new ArrayList<>();

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exercise> dExercises = new ArrayList<>();

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> dQuizs = new ArrayList<>();
}