package com.university.dto.request.admin;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhoaAminRequestDTO {

    @NotBlank(message = "Mã khoa không được để trống")
    private String maKhoa;
    @NotBlank(message = "Tên khoa không được để trống")
    private String tenKhoa;
    private String diaChi;
    private String moTa;
    @NotNull(message = "Id trường không được để trống")
    private UUID truongId;

}