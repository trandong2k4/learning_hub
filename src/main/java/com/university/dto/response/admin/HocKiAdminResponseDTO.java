package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HocKiAdminResponseDTO {

    private UUID id;
    private String maHocKi;
    private String tenHocKi;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayBatDau;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayKetThuc;

    public interface HocKiView {

        UUID getId();

        String getMaHocKi();

        String getTenHocKi();

        LocalDateTime getNgayBatDau();

        LocalDateTime getNgayKetThuc();
    }

}