package com.university.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "nhan_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 10, unique = true)
  private String maNhanVien;

  private LocalDateTime ngayNhanViec;

  private LocalDateTime ngayNghiViec;

  @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GiangDay> dGiangDays = new ArrayList<>();

  @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DanhGiaGiangVien> dDanhGiaGiangViens = new ArrayList<>();

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "users_id", nullable = false, unique = true)
  private Users users;

}
