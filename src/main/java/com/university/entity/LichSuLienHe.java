package com.university.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "lich_su_lien_he")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LichSuLienHe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 30, nullable = false)
    private String nguoiLienHe;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 10)
    private String soDienThoai;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime ngayLienHe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lien_he_id", nullable = false)
    private LienHe lienHe;

}