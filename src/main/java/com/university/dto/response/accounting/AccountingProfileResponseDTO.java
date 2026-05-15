package com.university.dto.response.accounting;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountingProfileResponseDTO {

    private UUID id;
    private String userName;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private GioiTinhEnum gioiTinh;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime ngaySinh;

    private String maNhanVien;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime ngayNhanViec;
}
