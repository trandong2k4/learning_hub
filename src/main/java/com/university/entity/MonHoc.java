package com.university.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mon_hoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 10, unique = true, nullable = false)
    private String maMonHoc;

    private String tenMonHoc;

    private Integer soTinChi;

    private String moTa;

    @OneToMany(mappedBy = "monHoc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChuongTrinhDaoTao> dChuongTrinhDaoTaos = new ArrayList<>();
    // Môn học có thể có nhiều môn tiên quyết
    @OneToMany(mappedBy = "monHoc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MonHocTienQuyet> dMonHocTienQuyets = new ArrayList<>();
    
}