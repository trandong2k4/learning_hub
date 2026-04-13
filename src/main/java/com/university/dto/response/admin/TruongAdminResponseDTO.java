package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TruongAdminResponseDTO {

    private UUID id;
    private String maTruong;
    private String tenTruong;
    private String diaChi;
    private String moTa;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayThanhLap;
    private String nguoiDaiDien;

    public interface TruongView {
        UUID getId();

        String getMaTruong();

        String getTenTruong();
    }

}