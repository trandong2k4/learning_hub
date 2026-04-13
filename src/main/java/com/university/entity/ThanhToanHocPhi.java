package com.university.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "thanh_toan_hoc_phi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToanHocPhi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    private LocalDateTime ngayThanhToan;

    @Column(nullable = false)
    private String fileChungTu;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_phi_id", nullable = false, unique = true)
    private HocPhi hocPhi;

}