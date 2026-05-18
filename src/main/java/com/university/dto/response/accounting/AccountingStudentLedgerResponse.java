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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountingStudentLedgerResponse {
    private UUID hocVienId;
    private String maHocVien;
    private String hocVienName;
    private String hocVienEmail;
    private Double tongCongNo;
    private Double tongDaThanhToan;
    private Double tongQuaHan;
    private Integer soKhoanCongNo;
    private List<AccountingHocPhiResponse> congNoItems;
    private List<AccountingHocPhiResponse> lichSuThanhToan;
}
