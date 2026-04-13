package com.university.dto.request.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.HocPhiEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocPhiAdminRequestDTO {

    @NotNull(message = "Số tiền không được để trống")
    private Double soTien;
    private HocPhiEnum trangThai;
    private Integer soTinChi;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime updatedAt;
    @NotNull(message = "Id học viên không được để trống")
    private UUID hocVienId;
    @NotNull(message = "Id học kì không được để trống")
    private UUID hocKiId;
}