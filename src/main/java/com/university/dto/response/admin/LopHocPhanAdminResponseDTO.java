package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.TrangThaiLHP;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LopHocPhanAdminResponseDTO {

    private UUID id;
    private String maLopHocPhan;
    private Integer soLuongToiDa;
    private Long soLuongDaDangKy;
    private TrangThaiLHP trangThai;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime hanDangKy;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime hanHuy;

    private UUID hocKiId;
    private String maHocKi;
    private String tenHocKi;

    private UUID monHocId;
    private String maMonHoc;
    private String tenMonHoc;
    private Integer soTinChi;

    public interface LopHocPhanView {
        UUID getId();
    }
}
