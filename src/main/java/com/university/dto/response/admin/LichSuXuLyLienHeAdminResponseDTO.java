package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.university.enums.TrangThaiXuLyLienHeEnum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LichSuXuLyLienHeAdminResponseDTO {

    private UUID id;
    private TrangThaiXuLyLienHeEnum trangThaiTruoc;
    private TrangThaiXuLyLienHeEnum trangThaiMoi;
    private String nguoiThucHien;
    private String ghiChu;
    private String noiDungPhanHoi;
    private LocalDateTime thoiGianXuLy;
}
