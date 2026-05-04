package com.university.dto.request.student;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ThongBaoRequest {

    @NotNull(message = "thongBaoNguoiDungId khong duoc de trong")
    private UUID thongBaoNguoiDungId;
}
