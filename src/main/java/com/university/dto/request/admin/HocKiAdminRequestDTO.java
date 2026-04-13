package com.university.dto.request.admin;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocKiAdminRequestDTO {

    @NotBlank(message = "Mã học kì không được đẻ trống")
    private String maHocKi;
    @NotBlank(message = "Tên học kì không được đẻ trống")
    private String tenHocKi;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayBatDau;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayKetThuc;
}
