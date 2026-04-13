package com.university.dto.request.admin;

import java.util.UUID;

import com.university.enums.CotDiemEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CotDiemAdminRequestDTO {

    @NotBlank(message = "Tên cột điểm không được để trống")
    private String tenCotDiem;
    @NotBlank(message = "Phần trăm tỉ trọng không được để trống")
    private String tiTrong;

    private CotDiemEnum loai;

    private Integer thuTuHienThi;

    @NotNull(message = "Id lớp học phần không được để trống")
    private UUID lopHocPhanId;
}