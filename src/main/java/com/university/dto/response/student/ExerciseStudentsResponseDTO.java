package com.university.dto.response.student;

import java.util.UUID;

import java.time.LocalDateTime;
import lombok.*;
import com.university.enums.ExerciseEnum;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseStudentsResponseDTO {
    private UUID id;
    private String tieude;
    private String moTa;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID lopHocPhanId;
    private String maLopHocPhan;

    private ExerciseEnum trangThai;
    private boolean dacoketqua;
    private Double diemSo;



}
