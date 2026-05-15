package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LichAdminResponseDTO {

    private UUID id;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayHoc;
    private String ghiChu;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private UUID gioHocId;
    private GioHocInfo gioHoc;
    private UUID phongId;
    private PhongInfo phong;
    private UUID lopHocPhanId;
    private LopHocPhanInfo lopHocPhan;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GioHocInfo {
        private UUID id;
        private String maGioHoc;
        private String tenGioHoc;
        private String gioBatDau;
        private String gioKetThuc;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhongInfo {
        private UUID id;
        private String maPhong;
        private String tenPhong;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LopHocPhanInfo {
        private UUID id;
        private String maLopHocPhan;
        private String tenMonHoc;
        private String tenHocKi;
    }
}
