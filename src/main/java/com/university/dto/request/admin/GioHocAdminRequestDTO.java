package com.university.dto.request.admin;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GioHocAdminRequestDTO {

    @NotBlank(message = "Mã giờ học không được đẻ trống")
    private String maGioHoc;
    @NotBlank(message = "Tên giờ học không được đẻ trống")
    private String tenGioHoc;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime thoiGianBatDau;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime thoiGianKetThuc;

}