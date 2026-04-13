package com.university.dto.request.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienAdminRequestDTO {

    @NotBlank(message = "Mã nhân viên không được để trống")
    private String maNhanVien;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayNhanViec;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayNghiViec;
    // Thêm users trước
    @NotNull(message = "Id users không được để trống")
    private UUID usersId;

}