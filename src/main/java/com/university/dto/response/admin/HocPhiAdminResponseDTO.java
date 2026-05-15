package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.HocPhiEnum;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HocPhiAdminResponseDTO {

    private UUID id;
    private Double soTien;
    private HocPhiEnum trangThai;
    private Integer soTinChi;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    private UUID hocVienId;
    private String hocVienMa;
    private String hocVienHoTen;
    private String hocVienEmail;
    private UUID hocKiId;
    private String hocKiMa;
    private String hocKiTen;

    private Double soTienConNo;
    private String thanhToanMaGiaoDich;
    private String thanhToanPhuongThuc;
    private LocalDateTime thanhToanNgay;

    public interface HocPhiView {
        UUID getId();
        Double getSoTien();
        HocPhiEnum getTrangThai();
        Integer getSoTinChi();
        LocalDateTime getCreatedAt();
        LocalDateTime getUpdatedAt();
        HocVienInfo getHocVien();
        HocKiInfo getHocKi();
        ThanhToanInfo getThanhToanHocPhi();

        interface HocVienInfo {
            UUID getId();
        }

        interface HocKiInfo {
            UUID getId();
        }

        interface ThanhToanInfo {
            String getMaGiaoDichGateway();
            String getPhuongThucThanhToan();
            LocalDateTime getNgayThanhToan();
        }
    }

    public interface DashboardTongQuan {
        Long getTongSoHocPhi();
        Double getTongSoTien();
        Long getSoChuaThanhToan();
        Long getSoDaThanhToan();
        Long getSoQuaHan();
    }

    public interface DashboardTheoHocKi {
        UUID getHocKiId();
        String getHocKiMa();
        String getHocKiTen();
        Long getSoLuong();
        Double getTongTien();
        Double getTienDaThu();
        Double getTienConNo();
        Long getSoChuaThanhToan();
        Long getSoDaThanhToan();
    }

    public interface DashboardTheoThang {
        Integer getNam();
        Integer getThang();
        Long getSoLuong();
        Double getTongTien();
        Double getTienDaThu();
    }

    public interface DashboardTopNo {
        UUID getHocVienId();
        String getHoTen();
        String getMaHocVien();
        Double getSoTienNo();
        Integer getSoLanNo();
    }

    public interface DangKyTinChiItem {
        UUID getId();
        UUID getHocVienId();
        String getHocVienMa();
        String getHocVienHoTen();
        UUID getHocKiId();
        String getHocKiMa();
        String getHocKiTen();
        Integer getSoTinChi();
        Double getSoTien();
        LocalDateTime getNgayDangKy();
    }

    public interface DangKyTinChiTongQuan {
        Long getTongSoHocVien();
        Long getTongSoDangKy();
        Long getTongSoTinChi();
        Double getTongSoTien();
        Long getTongSoHocKi();
    }

    public interface DangKyTinChiTheoHocKi {
        UUID getHocKiId();
        String getHocKiMa();
        String getHocKiTen();
        Integer getNamHoc();
        Long getSoHocVien();
        Long getSoDangKy();
        Long getTongTinChi();
        Double getTongTien();
    }

    public interface DangKyTinChiTheoNamHoc {
        Integer getNamHoc();
        Long getSoHocKi();
        Long getSoHocVien();
        Long getSoDangKy();
        Long getTongTinChi();
        Double getTongTien();
    }
}
