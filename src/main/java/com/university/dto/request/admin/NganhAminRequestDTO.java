package com.university.dto.request.admin;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NganhAminRequestDTO {

    @NotBlank(message = "Mã ngành không được để trống")
    private String maNganh;
    @NotBlank(message = "Tên ngành không được để trống")
    private String tenNganh;
    private String danhGia;
    private String moTa;
    @NotNull(message = "Id khoa không được để trống")
    private UUID khoaId;

}
