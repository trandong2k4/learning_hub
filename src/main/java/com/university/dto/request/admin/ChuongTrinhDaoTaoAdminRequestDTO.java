package com.university.dto.request.admin;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChuongTrinhDaoTaoAdminRequestDTO {

    @NotNull(message = "Mã ngành không được để trống")
    private String maNganh;

    @NotNull(message = "Mã môn học không được để trống")
    private String maMonHoc;
}
