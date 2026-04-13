package com.university.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.university.enums.LoaiBaiVietEnum;
import com.university.enums.TrangThaiBaiVietEnum;

@Entity
@Table(name = "bai_viet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaiViet {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String tieuDe;

  private String noiDung;

  private LocalDateTime ngayDang;

  private String tacGia;

  private String fileDinhKemUrl;

  private String hinhAnhUrl;

  @Enumerated(EnumType.STRING)
  private LoaiBaiVietEnum loaiBaiViet;

  @Enumerated(EnumType.STRING)
  private TrangThaiBaiVietEnum trangThai;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "users_id", nullable = false)
  private Users users;
}
