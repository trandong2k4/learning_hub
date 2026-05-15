package com.university.entity;

import com.university.enums.GioiTinhEnum;
import com.university.enums.TrangThaiXuLyLienHeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "phan_hoi_lien_he")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhanHoiLienHe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String hoTen;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 15)
    private String soDienThoai;

    @Column(length = 100, nullable = false)
    private String chuDe;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiXuLyLienHeEnum trangThai = TrangThaiXuLyLienHeEnum.CHUA_XU_LY;

    @Enumerated(EnumType.STRING)
    private GioiTinhEnum gioiTinh;

    @Column(length = 50)
    private String nguoiXuLy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime ngayTao;

    @UpdateTimestamp
    private LocalDateTime ngayCapNhat;

    @OneToMany(mappedBy = "phanHoiLienHe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LichSuXuLyLienHe> dSLichSuXuLyLienHes = new ArrayList<>();

    public void addLichSuXuLy(LichSuXuLyLienHe lichSu) {
        dSLichSuXuLyLienHes.add(lichSu);
        lichSu.setPhanHoiLienHe(this);
    }

    public void removeLichSuXuLy(LichSuXuLyLienHe lichSu) {
        dSLichSuXuLyLienHes.remove(lichSu);
        lichSu.setPhanHoiLienHe(null);
    }
}
