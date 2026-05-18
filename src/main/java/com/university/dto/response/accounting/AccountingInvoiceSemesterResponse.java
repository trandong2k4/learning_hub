package com.university.dto.response.accounting;

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
public class AccountingInvoiceSemesterResponse {
    private UUID hocKiId;
    private String hocKiMa;
    private String hocKiName;
    private Long soHocVienDangKy;
    private Long tongTinChi;
    private Double tongTienDuKien;
}
