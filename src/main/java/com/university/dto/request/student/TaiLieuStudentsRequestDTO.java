package com.university.dto.request.student;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TaiLieuStudentsRequestDTO {
    @NotNull(message = "Lớp học phần ID không được để trống")
    private UUID lophocphanId;
    @NotNull(message = "Tên tài liệu không được để trống")
    private String tenTaiLieu;
    @NotNull(message = "URL file tài liệu không được để trống")
    private String fileTaiLieuUrl;

}
