package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.TrangThaiLHP;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class DangKyTinChiAdminResponseDTO {

    public DangKyTinChiAdminResponseDTO(UUID id, UUID lopHocPhanId, String maLopHocPhan,
            UUID hocVienId, String maHocVien, UUID usersId, UUID monHocId,
            String maMonHoc, Integer soTinChi, LocalDateTime createdAt) {
        this.id = id;
        this.lopHocPhanId = lopHocPhanId;
        this.maLopHocPhan = maLopHocPhan;
        this.hocVienId = hocVienId;
        this.maHocVien = maHocVien;
        this.usersId = usersId;
        this.monHocId = monHocId;
        this.maMonHoc = maMonHoc;
        this.soTinChi = soTinChi;
        this.createdAt = createdAt;
    }

    public DangKyTinChiAdminResponseDTO(
            UUID id, UUID hocVienId, String maHocVien, String hoTen, String email, String soDienThoai,
            UUID lopHocPhanId, String maLopHocPhan, String tenMonHoc, Integer soTinChi, Double tienHocPhi,
            UUID hocKiId, String hocKiMa, String hocKiTen,
            LocalDateTime hanDangKy, LocalDateTime hanHuy, Integer soLuongToiDa, TrangThaiLHP trangThaiLopHocPhan,
            Long soLuongDaDangKy, LocalDateTime createdAt) {
        this.id = id;
        this.hocVienId = hocVienId;
        this.maHocVien = maHocVien;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.lopHocPhanId = lopHocPhanId;
        this.maLopHocPhan = maLopHocPhan;
        this.tenMonHoc = tenMonHoc;
        this.soTinChi = soTinChi;
        this.tienHocPhi = tienHocPhi;
        this.hocKiId = hocKiId;
        this.hocKiMa = hocKiMa;
        this.hocKiTen = hocKiTen;
        this.hanDangKy = hanDangKy;
        this.hanHuy = hanHuy;
        this.soLuongToiDa = soLuongToiDa;
        this.trangThaiLopHocPhan = trangThaiLopHocPhan != null ? trangThaiLopHocPhan.name() : null;
        this.soLuongDaDangKy = soLuongDaDangKy;
        this.createdAt = createdAt;
    }

    private UUID id;
    private UUID lopHocPhanId;
    private String maLopHocPhan;
    private UUID hocVienId;
    private String maHocVien;
    private UUID usersId;
    private UUID monHocId;
    private String maMonHoc;
    private Integer soTinChi;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    private String tenMonHoc;
    private UUID hocKiId;
    private String hocKiMa;
    private String hocKiTen;
    private String hoTen;
    private String email;
    private String soDienThoai;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime hanDangKy;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime hanHuy;

    private Integer soLuongToiDa;
    private Long soLuongDaDangKy;
    private String trangThaiLopHocPhan;

    private Double tienHocPhi;

    public interface DangKyTinChiDetail {
        UUID getId();

        LopHocPhanInfo getLopHocPhan();

        interface LopHocPhanInfo {
            UUID getId();
            String getMaLopHocPhan();
            MonHocInfo getMonHoc();
            HocKiInfo getHocKi();
            LocalDateTime getHanDangKy();
            LocalDateTime getHanHuy();
            Integer getSoLuongToiDa();

            interface MonHocInfo {
                UUID getId();
                String getMaMonHoc();
                String getTenMonHoc();
                Integer getSoTinChi();
            }

            interface HocKiInfo {
                UUID getId();
                String getMaHocKi();
                String getTenHocKi();
            }
        }

        HocVienInfo getHocVien();

        interface HocVienInfo {
            UUID getId();
            String getMaHocVien();
            UsersInfo getUsers();

            interface UsersInfo {
                UUID getId();
                String getHoTen();
                String getEmail();
                String getSoDienThoai();
            }
        }

        LocalDateTime getCreatedAt();
    }

    public interface LopHocPhanDangKyView {
        UUID getId();
        String getMaLopHocPhan();
        String getTenMonHoc();
        Integer getSoTinChi();
        String getHocKiMa();
        String getHocKiTen();
        Integer getNamHoc();
        String getTrangThai();
        Integer getSoLuongDaDangKy();
        Integer getSoLuongToiDa();
        LocalDateTime getHanDangKy();
    }

    public interface HocVienDangKyView {
        UUID getId();
        String getMaHocVien();
        String getHoTen();
        String getEmail();
        String getSoDienThoai();
        String getNganhTen();
    }
}
