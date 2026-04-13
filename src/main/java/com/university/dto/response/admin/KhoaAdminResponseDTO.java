package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhoaAdminResponseDTO {
    private UUID id;
    private String maKhoa;
    private String tenKhoa;
    private String diaChi;
    private String moTa;
    private UUID truongId;
    private String tenTruong;

    public interface KhoaView {
        UUID getId();
    }
}