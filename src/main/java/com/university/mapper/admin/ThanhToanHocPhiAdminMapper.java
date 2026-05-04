package com.university.mapper.admin;

import com.university.dto.request.admin.ThanhToanHocPhiAdminRequestDTO;
import com.university.dto.response.admin.ThanhToanHocPhiAdminResponseDTO;
import com.university.entity.ThanhToanHocPhi;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ThanhToanHocPhiAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity để lưu mới
     */
    public ThanhToanHocPhi toEntity(ThanhToanHocPhiAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ThanhToanHocPhi entity = new ThanhToanHocPhi();
        entity.setNgayThanhToan(dto.getNgayThanhToan());
        entity.setFileChungTu(dto.getFileChungTu());

        // Thiết lập thời gian tạo, nếu DTO không có thì lấy thời gian hiện tại
        entity.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());

        // hocPhiId sẽ được gán đối tượng HocPhi trong Service thông qua
        // HocPhiRepository

        return entity;
    }

    /**
     * Cập nhật thông tin Entity hiện có từ DTO
     */
    public void updateEntity(ThanhToanHocPhi entity, ThanhToanHocPhiAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setNgayThanhToan(dto.getNgayThanhToan());
        entity.setFileChungTu(dto.getFileChungTu());

        // Thường createdAt không cập nhật lại, nhưng nếu có field updatedAt bạn có thể
        // set ở đây
        // entity.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     */
    public ThanhToanHocPhiAdminResponseDTO toResponseDTO(ThanhToanHocPhi entity) {
        if (entity == null) {
            return null;
        }

        ThanhToanHocPhiAdminResponseDTO dto = new ThanhToanHocPhiAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setNgayThanhToan(entity.getNgayThanhToan());
        dto.setFileChungTu(entity.getFileChungTu());
        dto.setCreatedAt(entity.getCreatedAt());

        // Ánh xạ ID từ thực thể HocPhi (quan hệ) sang DTO
        if (entity.getHocPhi() != null) {
            dto.setHocPhiId(entity.getHocPhi().getId());
        }

        return dto;
    }
}