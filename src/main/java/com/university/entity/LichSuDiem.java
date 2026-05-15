package com.university.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lich_su_diem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichSuDiem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Float diemCu;

    @Column(nullable = false)
    private Float diemMoi;

    @Column(length = 500)
    private String ghiChu;

    @Column(nullable = false)
    private LocalDateTime thoiGianThayDoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diem_thanh_phan_id", nullable = false)
    private DiemThanhPhan diemThanhPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_thay_doi_id", nullable = false)
    private Users nguoiThayDoi;
}
