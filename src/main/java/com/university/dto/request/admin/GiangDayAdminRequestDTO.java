package com.university.dto.request.admin;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GiangDayAdminRequestDTO {

    private String vaiTro;
    @NotNull(message = "Id nhân viên không được để trống")
    private UUID nhanVienId;
    @NotNull(message = "Id lớp học phần không được để trống")
    private UUID lopHocPhanId;
}