package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LichAdminResponseDTO {

    private UUID id;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime ngayHoc;
    private String ghiChu;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime updatedAt;
    private UUID gioHocId;
    private UUID phongId;
    private UUID lopHocPhanId;

    public interface LichView {
        UUID getId();
    }

}