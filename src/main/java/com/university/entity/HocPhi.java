package com.university.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.university.enums.HocPhiEnum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hoc_phi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocPhi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Double soTien;

    @Enumerated(EnumType.STRING)
    private HocPhiEnum trangThai;

    private Integer soTinChi;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_vien_id", nullable = false)
    private HocVien hocVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_ki_id", nullable = false)
    private HocKi hocKi;

    @OneToOne(mappedBy = "hocPhi", cascade = CascadeType.ALL, orphanRemoval = true)
    private ThanhToanHocPhi thanhToanHocPhi;

}