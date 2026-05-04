package com.university.dto.request.admin;

import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersAdminRequestDTO {

    @NotBlank(message = "User không được để trống")
    private String userName;
    @NotBlank(message = "Password không được để trống")
    private String passWord;
    @Email(message = "Email phải là duy nhất")
    private String email;
    @NotBlank(message = "CCCD không được để trống, là duy nhất")
    private String cccd;
    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;
    private String diaChi;
    private GioiTinhEnum gioiTinh;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ngaySinh;
    @Length(max = 10, message = "Số điện thoại không quá 10 ki tự")
    private String soDienThoai;
    @NotNull(message = "Trạng thái của tài khoản phải là Khóa hoặc Mở")
    private Boolean trangThai;
    private String ghiChu;

}