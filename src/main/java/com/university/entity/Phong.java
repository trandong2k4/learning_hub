package com.university.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.university.enums.TinhTrangPhongEnum;

@Entity
@Table(name = "phong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phong {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 10, unique = true)
    private String maPhong;

    @Column(length = 30, nullable = false)
    private String tenPhong;

    private String toaNha;

    private Integer tang;

    private Integer sucChua;

    @Enumerated(EnumType.STRING)
    private TinhTrangPhongEnum tinhTrang;

    @OneToMany(mappedBy = "phong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lich> dLichs = new ArrayList<>();

}