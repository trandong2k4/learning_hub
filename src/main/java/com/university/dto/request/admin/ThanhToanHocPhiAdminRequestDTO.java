package com.university.dto.request.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToanHocPhiAdminRequestDTO {
    private LocalDate ngayThanhToan;
    @NotBlank(message = "Chứng từ hoặc mã giao dịch không được để trống")
    private String fileChungTu;
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String phuongThucThanhToan;
    private LocalDateTime createdAt;
    @NotNull(message = "Id học phí không được để trống")
    private UUID hocPhiId;
}
