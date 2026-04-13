package com.university.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "danh_gia_giang_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DanhGiaGiangVien {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private Float diemDanhGia;

  private String nhanXet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "nhan_vien_id", nullable = false)
  private NhanVien nhanVien;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lop_hoc_phan_id", nullable = false)
  private LopHocPhan lopHocPhan;
}
