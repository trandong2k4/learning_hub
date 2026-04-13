package com.university.dto.request.admin;

import com.university.enums.TinhTrangPhongEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhongAdminRequestDTO {

    @NotBlank(message = "Mã phòng không được để trống")
    private String maPhong;
    @NotBlank(message = "Tên phòng không được để trống")
    private String tenPhong;
    private String toaNha;
    private Integer tang;
    private Integer sucChua;
    private TinhTrangPhongEnum tinhTrang;
}