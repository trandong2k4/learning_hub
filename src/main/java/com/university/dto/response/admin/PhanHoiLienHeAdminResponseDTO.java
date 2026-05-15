package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.university.enums.GioiTinhEnum;
import com.university.enums.TrangThaiXuLyLienHeEnum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhanHoiLienHeAdminResponseDTO {

    private UUID id;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String chuDe;
    private String noiDung;
    private TrangThaiXuLyLienHeEnum trangThai;
    private GioiTinhEnum gioiTinh;
    private String nguoiXuLy;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private List<LichSuXuLyLienHeAdminResponseDTO> lichSuXuLys;

    public interface PhanHoiLienHeView {
        UUID getId();
    }
}
