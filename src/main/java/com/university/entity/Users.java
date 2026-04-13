package com.university.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.university.enums.GioiTinhEnum;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "username", length = 30, unique = true, nullable = false)
  private String userName;

  @Column(nullable = false)
  private String passWord;

  @Column(unique = true)
  private String email;

  @Column(nullable = false, unique = true)
  private String cccd;

  @Column(length = 30, nullable = false)
  private String hoTen;

  private String diaChi;

  @Enumerated(EnumType.STRING)
  private GioiTinhEnum gioiTinh;

  private LocalDateTime ngaySinh;

  @Column(length = 10)
  private String soDienThoai;

  @Column(nullable = false)
  private boolean trangThai;

  private String ghiChu;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createAt;

  @UpdateTimestamp
  private LocalDateTime updateAt;

  @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ThongBaoNguoiDung> dThongBaoNguoiDungs = new ArrayList<>();

  @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ThongBao> dThongBaos = new ArrayList<>();

  @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FileStorage> dFileStorages = new ArrayList<>();

  @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BaiViet> dBaiViets = new ArrayList<>();

  @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserRole> dUserRoles = new ArrayList<>();

  @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
  private NhanVien nhanVien;

  @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
  private HocVien hocVien;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getUsername() {
    return this.userName;
  }

  @Override
  public String getPassword() {
    return this.passWord;
  }

}
