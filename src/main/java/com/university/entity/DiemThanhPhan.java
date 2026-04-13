package com.university.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diem_thanh_phan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiemThanhPhan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Float diemSo;

    private Integer lanNhap;

    private String ghiChu;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dang_ky_tin_chi_id", nullable = false)
    private DangKyTinChi dangKyTinChi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cot_diem_id", nullable = false)
    private CotDiem cotDiem;
}
