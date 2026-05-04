package com.university.dto.request.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiemThanhPhanAdminRequestDTO {

    @Size(max = 10, min = 0, message = "Điểm không được nhỏ hơn 0 và lớn hơn 10")
    private BigDecimal diemSo;
    private Integer lanNhap;
    private String ghiChu;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime updatedAt;
    @NotNull(message = "Id đăng ký tín chỉ không được để trống")
    private UUID dangKyTinChiId;
    @NotNull(message = "Id cột điểm không được để trống")
    private UUID cotDiemId;

}