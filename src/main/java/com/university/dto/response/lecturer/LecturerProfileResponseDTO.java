package com.university.dto.response.lecturer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerProfileResponseDTO {
    private UUID id;

    private String userName;

    private String hoTen;

    private String diaChi;

    private String soDienThoai;

    private String email;

    private GioiTinhEnum gioiTinh;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime ngaySinh;

    private String cccd;

    private String maNhanVien;

    private LocalDateTime ngayNhanViec;

    private String avatarUrl;

    private List<LecturerScheduleDTO> schedule;

    public String getCccd() {
        if (cccd == null || cccd.length() < 4) return "****";
        return "*".repeat(cccd.length() - 4) + cccd.substring(cccd.length() - 4);
    }
}
