package com.university.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "nganh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Nganh {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 10, unique = true, nullable = false)
    private String maNganh;

    @Column(length = 100, nullable = false)
    private String tenNganh;

    private String danhGia;

    private String moTa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khoa_id", nullable = false)
    private Khoa khoa;

    @OneToMany(mappedBy = "nganh", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HocVien> dHocViens = new ArrayList<>();

    @OneToMany(mappedBy = "nganh", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChuongTrinhDaoTao> dChuongTrinhDaoTaos = new ArrayList<>();

}