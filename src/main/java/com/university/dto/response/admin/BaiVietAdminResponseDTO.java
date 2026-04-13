package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.LoaiBaiVietEnum;
import com.university.enums.TrangThaiBaiVietEnum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaiVietAdminResponseDTO {

    private UUID id;
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

    public interface BaiVietView {
        UUID getId();

        String getTieuDe();

        String getNoiDung();
    }

}