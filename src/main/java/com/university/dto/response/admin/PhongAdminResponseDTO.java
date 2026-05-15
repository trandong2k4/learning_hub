package com.university.dto.response.admin;

import java.util.UUID;
import com.university.enums.TinhTrangPhongEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhongAdminResponseDTO {

    private UUID id;
    private String maPhong;
    private String tenPhong;
    private String toaNha;
    private Integer tang;
    private Integer sucChua;
    private TinhTrangPhongEnum tinhTrang;
    private Integer soLichHoc;

    public interface PhongView {
        UUID getId();
        String getMaPhong();
        String getTenPhong();
        String getToaNha();
        Integer getTang();
        Integer getSucChua();
        TinhTrangPhongEnum getTinhTrang();
    }

}