package com.university.entity;

import com.university.enums.TrangThaiXuLyLienHeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lich_su_xu_ly_lien_he")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LichSuXuLyLienHe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiXuLyLienHeEnum trangThaiTruoc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiXuLyLienHeEnum trangThaiMoi;

    @Column(length = 100)
    private String nguoiThucHien;

    @Column(columnDefinition = "TEXT")
    private String ghiChu;

    @Column(columnDefinition = "TEXT")
    private String noiDungPhanHoi;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime thoiGianXuLy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phan_hoi_id", nullable = false)
    private PhanHoiLienHe phanHoiLienHe;
}
