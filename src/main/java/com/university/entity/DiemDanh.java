package com.university.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.university.enums.TrangThaiDiemDanhEnum;

@Entity
@Table(name = "diem_danh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiemDanh {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiDiemDanhEnum trangThai;

    private String ghiChu;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_vien_id", nullable = false)
    private HocVien hocVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lich_id", nullable = false)
    private Lich lich;

}
