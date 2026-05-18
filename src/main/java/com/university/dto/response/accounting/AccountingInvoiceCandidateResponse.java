package com.university.dto.response.accounting;

import com.university.enums.HocPhiEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountingInvoiceCandidateResponse {
    private UUID hocVienId;
    private String maHocVien;
    private String hocVienName;
    private String hocVienEmail;
    private Integer tongSoTinChi;
    private Double soTien;
    private Boolean daCoHoaDon;
    private UUID hocPhiId;
    private HocPhiEnum trangThaiHocPhi;
}
