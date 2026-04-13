package com.university.dto.request.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DangKyTinChiAdminRequestDTO {

    @NotNull(message = "Id lớp học phần không được để trống")
    private UUID lopHocPhanId;
    @NotNull(message = "Id học viên không được để trống")
    private UUID hocVienId;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
}