package com.university.dto.request.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.LoaiBaiVietEnum;
import com.university.enums.TrangThaiBaiVietEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaiVietAdminRequestDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String tieuDe;
    private String noiDung;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayDang;
    private String tacGia;
    private String fileDinhKemUrl;
    private String hinhAnhUrl;
    private LoaiBaiVietEnum loaiBaiViet;
    private TrangThaiBaiVietEnum trangThai;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime updatedAt;
    @NotNull(message = "UserId không được để trống")
    private UUID usersId;

}