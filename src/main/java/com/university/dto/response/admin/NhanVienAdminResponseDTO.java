package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NhanVienAdminResponseDTO {

    private UUID id;
    private String maNhanVien;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayNhanViec;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayNghiViec;
    private UUID usersId;

    public interface NhanVienView {
        UUID getId();
    }

}