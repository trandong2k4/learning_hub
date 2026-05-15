package com.university.dto.request.admin;

import com.university.enums.GioiTinhEnum;
import com.university.enums.TrangThaiXuLyLienHeEnum;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhanHoiLienHeAdminRequestDTO {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 50, message = "Email tối đa 50 ký tự")
    private String email;

    @Pattern(regexp = "^(0[35789]\\d{8}|\\+84[35789]\\d{8}|\\+[1-9]\\d{8,13})?$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @NotBlank(message = "Chủ đề không được để trống")
    @Size(max = 100, message = "Chủ đề tối đa 100 ký tự")
    private String chuDe;

    @NotBlank(message = "Nội dung không được để trống")
    private String noiDung;

    private GioiTinhEnum gioiTinh;

    private TrangThaiXuLyLienHeEnum trangThai;
}
