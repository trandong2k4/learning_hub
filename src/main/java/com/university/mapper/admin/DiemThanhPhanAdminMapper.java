package com.university.mapper.admin;

import com.university.dto.request.admin.DiemThanhPhanAdminRequestDTO;
import com.university.dto.response.admin.DiemThanhPhanAdminResponseDTO;
import com.university.entity.DiemThanhPhan;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DiemThanhPhanAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity (Dùng cho tạo mới)
     */
    public DiemThanhPhan toEntity(DiemThanhPhanAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        DiemThanhPhan entity = new DiemThanhPhan();
        if (dto.getDiemSo() != null) {
            entity.setDiemSo(dto.getDiemSo().floatValue());
        }
        entity.setLanNhap(dto.getLanNhap());
        entity.setGhiChu(dto.getGhiChu());

        // Không set DangKyTinChi và CotDiem ở đây — sẽ gán từ Repository trong Service
        // Không set updatedAt ở đây — sẽ được xử lý khi lưu/cập nhật

        return entity;
    }

    public void updateEntity(DiemThanhPhan entity, DiemThanhPhanAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getDiemSo() != null) {
            entity.setDiemSo(dto.getDiemSo().floatValue());
        }

        entity.setLanNhap(dto.getLanNhap());
        entity.setGhiChu(dto.getGhiChu());
        entity.setUpdatedAt(LocalDateTime.now());
    }

    public DiemThanhPhanAdminResponseDTO toResponseDTO(DiemThanhPhan entity) {
        if (entity == null) {
            return null;
        }

        DiemThanhPhanAdminResponseDTO dto = new DiemThanhPhanAdminResponseDTO();
        dto.setId(entity.getId());
        if (entity.getDiemSo() != null) {
            dto.setDiemSo(BigDecimal.valueOf(entity.getDiemSo()));
        }
        dto.setLanNhap(entity.getLanNhap());
        dto.setGhiChu(entity.getGhiChu());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Ánh xạ ID từ các đối tượng quan hệ (Relation)
        if (entity.getDangKyTinChi() != null) {
            dto.setDangKyTinChiId(entity.getDangKyTinChi().getId());
        }

        if (entity.getCotDiem() != null) {
            dto.setCotDiemId(entity.getCotDiem().getId());
        }

        return dto;
    }
}