package com.university.dto.request.student;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DanhGiaGiangVienDraftRequest {

    @NotNull
    private UUID lopHocPhanId;

    @Min(1)
    @Max(5)
    @NotNull
    private Integer diemDanhGia;

    // nhanXet optional cho draft
    private String nhanXet;
}
