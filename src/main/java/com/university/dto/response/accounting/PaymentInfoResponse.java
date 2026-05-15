package com.university.dto.response.accounting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfoResponse {
    private UUID paymentId;
    private UUID hocPhiId;
    private UUID hocVienId;
    private String hocVienName;
    private Double amount;
    private LocalDateTime ngayThanhToan;
    private String phuongThucThanhToan;
    private String maGiaoDichGateway;
    private String fileChungTu;
}
