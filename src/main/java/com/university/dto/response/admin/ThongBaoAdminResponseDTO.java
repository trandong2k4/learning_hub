package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.entity.Users;
import com.university.enums.LoaiThongBaoEnum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThongBaoAdminResponseDTO {

    private UUID id;
    private String tieuDe;
    private String noiDung;
    private String fileThongBao;
    private LoaiThongBaoEnum loaiThongBao;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
    private Users usersId;

    public interface ThongBaoView {
        UUID getId();
    }
}