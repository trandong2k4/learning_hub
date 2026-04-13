package com.university.dto.request.admin;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.entity.Users;
import com.university.enums.LoaiThongBaoEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThongBaoAdminRequestDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String tieuDe;
    private String noiDung;
    private String fileThongBao;
    private LoaiThongBaoEnum loaiThongBao;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
    @NotNull(message = "Id users không được để trống")
    private Users usersId;
}