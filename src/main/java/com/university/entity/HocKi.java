package com.university.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hoc_ki")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocKi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 10, unique = true, nullable = false)
    private String maHocKi;

    @Column(length = 30, nullable = false)
    private String tenHocKi;

    private LocalDateTime ngayBatDau;

    private LocalDateTime ngayKetThuc;

    @OneToMany(mappedBy = "hocKi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LopHocPhan> dLopHocPhans = new ArrayList<>();

    @OneToMany(mappedBy = "hocKi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HocPhi> dHocPhis = new ArrayList<>();
}