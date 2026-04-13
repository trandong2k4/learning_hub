package com.university.dto.request.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToanHocPhiAdminRequestDTO {
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayThanhToan;
    @NotBlank(message = "Đường dẫm File chứng từ không được để trống")
    private String fileChungTu;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
    @NotNull(message = "Id học phí không được để trống")
    private UUID hocPhiId;
}