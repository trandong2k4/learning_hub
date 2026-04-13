package com.university.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lien_he")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LienHe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 30, nullable = false, unique = true)
    private String tenLienHe;

    @Column(nullable = false)
    private String fanPageUrl;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 10, unique = true)
    private String soDienThoai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khoa_id", nullable = false)
    private Khoa khoa;

    @OneToMany(mappedBy = "lienHe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LichSuLienHe> dLichSuLienHes = new ArrayList<>();

}