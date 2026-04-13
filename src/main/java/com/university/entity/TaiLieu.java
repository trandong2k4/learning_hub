package com.university.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.university.enums.TaiLieuEnum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tai_lieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaiLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String tenTaiLieu;

    private String moTa;
    

    @Column(nullable = false)
    private String fileTaiLieuUrl;

    @Column(nullable = false)
    private TaiLieuEnum loaiTaiLieu;

    @Column(nullable = false)
    private LocalDateTime ngayDang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lop_hoc_phan_id", nullable = false)
    private LopHocPhan lopHocPhan;
}
