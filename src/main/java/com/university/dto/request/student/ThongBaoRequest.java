package com.university.dto.request.student;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ThongBaoRequest {
    @NotNull(message = "usersId không được để trống")
    private UUID usersId;
    @NotNull(message = "thongbaonguoidungId không được để trống")
    private UUID thongbaonguoidungId;
}
