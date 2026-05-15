package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private UUID usersId;
    private String userName;
    private String hoTen;
    private Long soNguoiNhan;
    private Long soNguoiDaNhan;

    public interface ThongBaoView {
        UUID getId();
    }
}
