package com.university.dto.request.student;

import java.time.LocalDateTime;

import com.university.enums.GioiTinhEnum;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocVienProfileRequestDTO {
    private String HoTen;
    private String DiaChi;
    private String SoDienThoai;
    private String Email;
    private GioiTinhEnum GioiTinh;
    private LocalDateTime NgaySinh;
    private String Cccd;
}
