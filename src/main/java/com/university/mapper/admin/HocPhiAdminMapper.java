package com.university.mapper.admin;

import com.university.dto.response.admin.HocPhiAdminResponseDTO;
import com.university.entity.HocPhi;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HocPhiAdminMapper {

    // Giả sử bạn có HocPhiAdminRequestDTO tương ứng
    // public HocPhi toEntity(HocPhiAdminRequestDTO dto) { ... }

    /**
     * Chuyển từ Entity sang Response DTO
     */
    public HocPhiAdminResponseDTO toResponseDTO(HocPhi entity) {
        if (entity == null)
            return null;

        HocPhiAdminResponseDTO dto = new HocPhiAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setSoTien(entity.getSoTien());
        dto.setTrangThai(entity.getTrangThai());
        dto.setSoTinChi(entity.getSoTinChi());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Ánh xạ ID từ quan hệ HocVien
        if (entity.getHocVien() != null) {
            dto.setHocVienId(entity.getHocVien().getId());
        }

        // Ánh xạ ID từ quan hệ HocKi
        if (entity.getHocKi() != null) {
            dto.setHocKiId(entity.getHocKi().getId());
        }

        return dto;
    }

    /**
     * Cập nhật thời gian và trạng thái (Ví dụ dùng trong Service)
     */
    public void updateAudit(HocPhi entity) {
        if (entity != null) {
            entity.setUpdatedAt(LocalDateTime.now());
        }
    }
}