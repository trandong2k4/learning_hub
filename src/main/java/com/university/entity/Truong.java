package com.university.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "truong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Truong {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ma_truong", length = 10, unique = true, nullable = false)
    private String maTruong;

    @Column(name = "ten_truong", length = 100)
    private String tenTruong;

    @Column(length = 50)
    private String diaChi;

    private String moTa;

    private LocalDateTime ngayThanhLap;

    @Column(length = 30)
    private String nguoiDaiDien;

    @OneToMany(mappedBy = "truong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Khoa> dKhoas = new ArrayList<>();
}