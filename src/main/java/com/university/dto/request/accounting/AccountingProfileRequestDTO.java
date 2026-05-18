package com.university.dto.request.accounting;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountingProfileRequestDTO {

    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private String diaChi;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    private GioiTinhEnum gioiTinh;

    @Past(message = "Ngày sinh phải ở quá khứ")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    private String avatarUrl;
}
