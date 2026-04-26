package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DanhGiaGiangVienAdminResponseDTO {

    private UUID id;
    private Float diemDanhGia;
    private String nhanXet;
    private UUID nhanVienId;
    private UUID lopHocPhanId;

    public interface DanhGiaGiangVienView {

        UUID getId();

        Float getDiemDanhGia();

        String getNhanXet();

        UUID getNhanVienId();

        UUID getLopHocPhanId();
    }

}