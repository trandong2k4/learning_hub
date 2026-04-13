package com.university.dto.request.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichAdminRequestDTO {

    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayHoc;
    private String ghiChu;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime updatedAt;

    @NotNull(message = "Id giờ học không được để trống")
    private UUID gioHocId;
    @NotNull(message = "Id phòng học không được để trống")
    private UUID phongId;
    @NotNull(message = "Id lớp học phần không được để trống")
    private UUID lopHocPhanId;

}