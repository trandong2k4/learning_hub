package com.university.dto.response.admin;

import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GioHocAdminResponseDTO {

    private UUID id;
    private String maGioHoc;
    private String tenGioHoc;
    @JsonFormat(pattern = "hh:mm:ss")
    private LocalTime thoiGianBatDau;
    @JsonFormat(pattern = "hh:mm:ss")
    private LocalTime thoiGianKetThuc;

    public interface TruongView {
        UUID getId();
    }

}