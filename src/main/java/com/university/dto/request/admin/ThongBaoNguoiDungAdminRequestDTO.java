package com.university.dto.request.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThongBaoNguoiDungAdminRequestDTO {

    private Boolean daNhan;

    @NotNull(message = "Id người dùng không được để trống")
    private UUID userId;

    @NotNull(message = "Id thông báo không được để trống")
    private UUID thongBaoId;
}
