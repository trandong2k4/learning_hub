package com.university.dto.request.student;


import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * DTO chứa dữ liệu học viên gửi lên khi cập nhật hồ sơ cá nhân.
 *
 * <p>Các trường được validate bởi Bean Validation trước khi đến Service layer.
 * Mã học viên và ngành học không nằm trong DTO này vì học viên không được tự thay đổi.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocVienProfileRequestDTO {

    /** Họ và tên đầy đủ, không được để trống. */
    @NotBlank(message = "Ho ten khong duoc de trong")
    private String hoTen;

    /** Địa chỉ thường trú, không bắt buộc. */
    private String diaChi;

    /** Số điện thoại, phải đủ 10 chữ số. */
    @Pattern(regexp = "^[0-9]{10}$", message = "So dien thoai phai co 10 chu so")
    private String soDienThoai;

    /** Địa chỉ email hợp lệ, không bắt buộc. */
    @Email(message = "Email khong hop le")
    private String email;

    /** Giới tính (NAM / NU), không bắt buộc. */
    private GioiTinhEnum gioiTinh;

    /** Ngày sinh, phải là ngày trong quá khứ. */
    @Past(message = "Ngay sinh phai o qua khu")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    /** Số căn cước công dân, phải đủ 12 chữ số. */
    @Pattern(regexp = "^[0-9]{12}$", message = "CCCD phai co 12 chu so")
    private String cccd;
}