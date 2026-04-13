package com.university.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "giang_day")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GiangDay {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  
  private String vaiTro;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "nhan_vien_id", nullable = false)
  private NhanVien nhanVien;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lop_hoc_phan_id", nullable = false)
  private LopHocPhan lopHocPhan;
}
