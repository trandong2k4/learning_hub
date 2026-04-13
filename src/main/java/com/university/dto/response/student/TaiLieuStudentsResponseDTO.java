package com.university.dto.response.student;

import java.time.LocalDateTime;
import java.util.UUID;

import com.university.enums.TaiLieuEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class TaiLieuStudentsResponseDTO {
    private UUID id; ;
    private String tenTaiLieu;
    private String moTa;
    private String fileTaiLieuUrl;
    private TaiLieuEnum loaiTaiLieu;
    private LocalDateTime ngayDang;
    private UUID lopHocPhanId;
}
