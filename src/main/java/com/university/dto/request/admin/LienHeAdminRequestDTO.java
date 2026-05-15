package com.university.dto.request.admin;

import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LienHeAdminRequestDTO {

    @NotBlank(message = "Tên lên hệ không được để trống")
    private String tenLienHe;
    @NotBlank(message = "FanPageUrl không được để trống")
    private String fanPageUrl;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không được để trống, đúng định dạng")
    private String email;
    @Pattern(
        regexp = "^(0[35789]\\d{8}|\\+84[35789]\\d{8}|\\+[1-9]\\d{8,13})$",
        message = "Số điện thoại không hợp lệ (VD: 0912345678 hoặc +84912345678)"
    )
    private String soDienThoai;
    @NotNull(message = "Id khoa không được để trống")
    private UUID khoaId;

}
