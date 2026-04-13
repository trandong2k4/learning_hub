package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LichSuLienHeAdminResponseDTO {

    private UUID id;
    private String nguoiLienHe;
    private String email;
    private String soDienThoai;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayLienHe;
    private UUID lienHeId;

    public interface LichSuLienHeView {
        UUID getId();
    }

}