package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HocVienAdminResponseDTO {

    private UUID id;
    private String maHocVien;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayNhapHoc;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayTotNghiep;
    private UUID nganhId;

    public interface HocVienView {
        UUID getId();
    }

}