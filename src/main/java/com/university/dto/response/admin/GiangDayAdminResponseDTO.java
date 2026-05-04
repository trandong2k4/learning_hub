package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GiangDayAdminResponseDTO {

    private UUID id;
    private String vaiTro;
    private UUID nhanVienId;
    private UUID lopHocPhanId;

    public interface GiangDayView {

        UUID getId();

        String getVaiTro();

        NhanVienInfo getNhanVien();

        LopHocPhanInfo getLopHocPhan();

        interface NhanVienInfo {
            UUID getId();
        }

        interface LopHocPhanInfo {
            UUID getId();
        }
    }

}