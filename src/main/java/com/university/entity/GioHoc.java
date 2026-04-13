package com.university.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "gio_hoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GioHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 10, unique = true)
    private String maGioHoc;

    @Column(length = 30, nullable = false)
    private String tenGioHoc;

    private LocalTime thoiGianBatDau;

    private LocalTime thoiGianKetThuc;

    @OneToMany(mappedBy = "gioHoc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lich> dLichs = new ArrayList<>();

}