package com.university.dto.request.admin;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThongBaoNguoiDungAdminRequestDTO {

    private Boolean daNhan;
    @NotNull(message = "Id users không được để trống")
    private UUID userId;
    @NotNull(message = "Id thông báo không được để trống")
    private UUID thongBaoId;
}