package com.university.dto.request.student;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DanhGiaGiangVienStudentRequest {

    @NotNull
    private UUID lopHocPhanId;

    @Min(1)
    @Max(5)
    @NotNull
    private Integer diemDanhGia;

    @NotBlank
    private String nhanXet;
}
