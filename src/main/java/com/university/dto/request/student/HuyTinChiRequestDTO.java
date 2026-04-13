package com.university.dto.request.student;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HuyTinChiRequestDTO {
    @NotNull(message = "không được để trống")
    private UUID hocVienId;
    @NotNull(message = "không được để trống")
    private UUID lopHocPhanId;
}
