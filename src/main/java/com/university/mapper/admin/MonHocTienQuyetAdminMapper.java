package com.university.mapper.admin;

import com.university.dto.request.admin.MonHocTienQuyetAdminRequestDTO;
import com.university.dto.response.admin.MonHocTienQuyetAdminResponseDTO;
import com.university.entity.MonHocTienQuyet;
import org.springframework.stereotype.Component;

@Component
public class MonHocTienQuyetAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity để lưu mới
     */
    public MonHocTienQuyet toEntity(MonHocTienQuyetAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        MonHocTienQuyet entity = new MonHocTienQuyet();
        entity.setMaMonHoc(dto.getMaMonHoc());

        // monHocId sẽ được gán đối tượng MonHoc trong Service thông qua
        // MonHocRepository

        return entity;
    }

    /**
     * Cập nhật thông tin Entity hiện có từ DTO
     */
    public void updateEntity(MonHocTienQuyet entity, MonHocTienQuyetAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setMaMonHoc(dto.getMaMonHoc());

        // Việc thay đổi môn học chính (MonHoc) nên được xử lý ở tầng Service
        // entity.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     * Giả định Response DTO có cấu trúc tương tự Request
     */
    public MonHocTienQuyetAdminResponseDTO toResponseDTO(MonHocTienQuyet entity) {
        if (entity == null) {
            return null;
        }

        MonHocTienQuyetAdminResponseDTO dto = new MonHocTienQuyetAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setMaMonHoc(entity.getMaMonHoc());

        if (entity.getMonHoc() != null) {
            dto.setMonHocId(entity.getMonHoc().getId());
            dto.setMaMonHoc(entity.getMonHoc().getMaMonHoc());
            dto.setTenMonHoc(entity.getMonHoc().getTenMonHoc());
        }
        if (entity.getMonTienQuyet() != null) {
            dto.setMonTienQuyetId(entity.getMonTienQuyet().getId());
            dto.setMaTienQuyet(entity.getMonTienQuyet().getMaMonHoc());
            dto.setTenMonTienQuyet(entity.getMonTienQuyet().getTenMonHoc());
        }

        return dto;
    }
}