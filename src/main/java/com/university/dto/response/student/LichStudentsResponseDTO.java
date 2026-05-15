package com.university.dto.response.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichStudentsResponseDTO {

    private String thu;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ngayHoc;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime thoiGianBatDau;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime thoiGianKetThuc;

    private String tenGioHoc;
    private String maPhong;
    private String tenPhong;
}
