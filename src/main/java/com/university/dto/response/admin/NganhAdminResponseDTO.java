package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NganhAdminResponseDTO {
    private UUID id;
    private String maNganh;
    private String tenNganh;
    private String danhGia;
    private String moTa;
    private UUID khoaId;
    private String tenKhoa;

    public interface NganhView {
        UUID getId();

        String getMaNganh();
    }
}