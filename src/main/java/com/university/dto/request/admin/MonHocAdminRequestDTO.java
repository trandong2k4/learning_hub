package com.university.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonHocAdminRequestDTO {

    @NotBlank(message = "Tên môn học không được để trống")
    private String maMonHoc;
    @NotBlank(message = "Tên môn học không được để trống")
    private String tenMonHoc;
    private Integer soTinChi;
    private String moTa;
}