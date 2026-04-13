package com.university.dto.request.admin;

import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichSuLienHeAdminRequestDTO {

    @NotBlank(message = "Người liên hệ không được để trống")
    private String nguoiLienHe;
    @Email(message = "Email không được để trống, đúng định dạng")
    private String email;
    @Length(max = 10, message = "Số điện thoại không quá 10 chữ số")
    private String soDienThoai;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayLienHe;
    @NotNull(message = "Id liên hệ không được để trống")
    private UUID lienHeId;
}