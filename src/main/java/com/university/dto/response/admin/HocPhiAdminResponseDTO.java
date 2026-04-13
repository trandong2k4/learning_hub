package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.HocPhiEnum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HocPhiAdminResponseDTO {

    private UUID id;
    private Double soTien;
    private HocPhiEnum trangThai;
    private Integer soTinChi;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime updatedAt;
    private UUID hocVienId;
    private UUID hocKiId;

    public interface HocPhiView {
        UUID getId();
    }

}