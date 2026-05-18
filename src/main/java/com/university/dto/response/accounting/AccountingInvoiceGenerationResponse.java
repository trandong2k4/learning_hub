package com.university.dto.response.accounting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountingInvoiceGenerationResponse {
    private UUID hocKiId;
    private String hocKiMa;
    private String hocKiName;
    private Integer tongHocVien;
    private Integer soHoaDonDaTao;
    private Integer soHoaDonBoQua;
    private Double tongTienDaTao;
    private List<AccountingInvoiceCandidateResponse> items;
}
