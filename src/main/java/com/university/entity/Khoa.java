package com.university.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "khoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Khoa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 10, unique = true, nullable = false)
    private String maKhoa;

    @Column(length = 100, nullable = false)
    private String tenKhoa;

    @Column(length = 100, nullable = false)
    private String diaChi;

    private String moTa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truong_id", nullable = false)
    private Truong truong;

    @OneToMany(mappedBy = "khoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Nganh> dNganhs = new ArrayList<>();

    @OneToMany(mappedBy = "khoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LienHe> dLienHes = new ArrayList<>();

}