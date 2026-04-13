package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThanhToanHocPhiAdminResponseDTO {

    private UUID id;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayThanhToan;
    private String fileChungTu;
    private LocalDateTime createdAt;
    private UUID hocPhiId;

    public interface ThanhToanHocPhiView {
        UUID getId();
    }

}