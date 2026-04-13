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

    public interface PhongView {
        UUID getId();
    }

}