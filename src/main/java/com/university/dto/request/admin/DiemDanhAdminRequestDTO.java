package com.university.dto.request.admin;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiemDanhAdminRequestDTO {

    @NotNull(message = "Trạng thái phải là true or false")
    private Boolean trangThai;
    @NotNull(message = "Id học viên không được để trống")
    private UUID hocVienId;
    @NotNull(message = "Id lịch không được để trống")
    private UUID lichId;
}