package com.university.dto.response.admin;

import java.util.UUID;

import com.university.enums.TrangThaiDiemDanhEnum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiemDanhAdminResponseDTO {

    private UUID id;
    private TrangThaiDiemDanhEnum trangThai;
    private UUID hocVienId;
    private UUID lichId;

    public interface DiemDanhView {

        UUID getId();

        TrangThaiDiemDanhEnum getTrangThai();

        UUID getHocVienId();

        UUID getLichId();
    }
}