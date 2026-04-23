package com.university.dto.request.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassWordResetTokenAdminRequestDTO {

    @NotBlank(message = "Token không được đẻ trống")
    private String token; // Chuỗi UUID ngẫu nhiên

    @NotNull(message = "Id users không được đẻ trống")
    private UUID usersId;

    @NotBlank(message = "Thời hạn dùng không được đẻ trống")
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime expiryDate; // Thời gian hết hạn (ví dụ: +15 phút)

}
