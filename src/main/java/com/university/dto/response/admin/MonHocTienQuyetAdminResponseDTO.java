package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonHocTienQuyetAdminResponseDTO {

    private UUID id;
    private UUID monHocId;
    private String maMonHoc;
    private String tenMonHoc;
    private UUID monTienQuyetId;
    private String maTienQuyet;
    private String tenMonTienQuyet;

    public interface MonHocTienQuyetView {
        UUID getId();
    }

}