package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChuongTrinhDaoTaoAdminResponseDTO {

    private UUID id;
    private UUID monHocId;
    private String maMonHoc;
    private String tenMonHoc;
    private Integer soTinChi;
    private String moTa;
    private String maNganh;

    public interface ChuongTrinhDaoTaoView {

        UUID getId();

        NganhInfo getNganh();

        MonHocInfo getMonHoc();

        interface NganhInfo {
            UUID getId();
        }

        interface MonHocInfo {
            UUID getId();
        }
    }

}