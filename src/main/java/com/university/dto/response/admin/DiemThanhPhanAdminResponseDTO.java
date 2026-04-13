package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiemThanhPhanAdminResponseDTO {

    private UUID id;
    private Float diemSo;
    private Integer lanNhap;
    private String ghiChu;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime updatedAt;
    private UUID dangKyTinChiId;
    private UUID cotDiemId;

    public interface DiemThanhPhanView {
        UUID getId();
    }

}