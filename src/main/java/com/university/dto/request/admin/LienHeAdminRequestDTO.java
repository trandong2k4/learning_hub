package com.university.dto.request.admin;

import java.util.UUID;

import org.hibernate.validator.constraints.Length;

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
    @Email(message = "Email không được để trống, đúng định dạng")
    private String email;
    @Length(max = 10, message = "Số điện thoại không quá 10 chữ số")
    private String soDienThoai;
    @NotNull(message = "Id khoa không được để trống")
    private UUID khoaId;

}