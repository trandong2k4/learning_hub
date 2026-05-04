package com.university.mapper.admin;

import com.university.dto.request.admin.LienHeAdminRequestDTO;
import com.university.dto.response.admin.LienHeAdminResponseDTO;
import com.university.entity.LienHe;
import org.springframework.stereotype.Component;

@Component
public class LienHeAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity để lưu mới
     */
    public LienHe toEntity(LienHeAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        LienHe entity = new LienHe();
        entity.setTenLienHe(dto.getTenLienHe());
        entity.setFanPageUrl(dto.getFanPageUrl());
        entity.setEmail(dto.getEmail());
        entity.setSoDienThoai(dto.getSoDienThoai());

        // khoaId sẽ được gán đối tượng Khoa trong Service thông qua KhoaRepository

        return entity;
    }

    /**
     * Cập nhật thông tin Entity hiện có từ DTO
     */
    public void updateEntity(LienHe entity, LienHeAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTenLienHe(dto.getTenLienHe());
        entity.setFanPageUrl(dto.getFanPageUrl());
        entity.setEmail(dto.getEmail());
        entity.setSoDienThoai(dto.getSoDienThoai());

        // Việc thay đổi Khoa (nếu cần) nên được xử lý ở tầng Service
        // entity.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     */
    public LienHeAdminResponseDTO toResponseDTO(LienHe entity) {
        if (entity == null) {
            return null;
        }

        LienHeAdminResponseDTO dto = new LienHeAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setTenLienHe(entity.getTenLienHe());
        dto.setFanPageUrl(entity.getFanPageUrl());
        dto.setEmail(entity.getEmail());
        dto.setSoDienThoai(entity.getSoDienThoai());

        // Ánh xạ ID từ thực thể Khoa sang DTO
        if (entity.getKhoa() != null) {
            dto.setKhoaId(entity.getKhoa().getId());
        }

        return dto;
    }
}