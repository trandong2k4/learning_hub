package com.university.dto.request.admin;

import java.time.LocalDate;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.dto.request.admin.warrap.GioiTinhEnumConverter;
import com.university.enums.GioiTinhEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersAdminRequestDTO {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @ExcelProperty(index = 0)
    private String userName;
    @ExcelProperty(index = 1)
    private String passWord;
    @Email(message = "Email không đúng định dạng")
    @ExcelProperty(index = 2)
    private String email;
    @NotBlank(message = "CCCD không được để trống")
    @ExcelProperty(index = 3)
    private String cccd;
    @NotBlank(message = "Họ tên không được để trống")
    @ExcelProperty(index = 4)
    private String hoTen;
    @ExcelProperty(index = 5)
    private String diaChi;
    @ExcelProperty(index = 6, converter = GioiTinhEnumConverter.class)
    private GioiTinhEnum gioiTinh;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty(index = 7)
    private LocalDate ngaySinh;
    @Pattern(regexp = "^(0[35789]\\d{8}|\\+84[35789]\\d{8}|\\+[1-9]\\d{8,13})$", message = "Số điện thoại không hợp lệ (VD: 0912345678 hoặc +84912345678)")
    @ExcelProperty(index = 8)
    private String soDienThoai;
    @NotNull(message = "Trạng thái của tài khoản phải là Khóa hoặc Mở")
    @ExcelProperty(index = 9)
    private Boolean trangThai;
    @ExcelProperty(index = 10)
    private String ghiChu;
    @ExcelProperty(index = 11)
    private String maRole;

}
