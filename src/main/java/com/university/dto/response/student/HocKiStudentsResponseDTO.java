package com.university.dto.response.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocKiStudentsResponseDTO {

    private UUID id;
    private String maHocKi;
    private String tenHocKi;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayBatDau;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayKetThuc;

    private boolean dangHoatDong;
    private int nam;
    private int maxTinChi;
}
