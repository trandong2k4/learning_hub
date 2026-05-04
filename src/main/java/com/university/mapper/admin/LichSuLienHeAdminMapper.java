package com.university.mapper.admin;

import com.university.dto.request.admin.LichSuLienHeAdminRequestDTO;
import com.university.dto.response.admin.LichSuLienHeAdminResponseDTO;
import com.university.entity.LichSuLienHe;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LichSuLienHeAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity để lưu mới
     */
    public LichSuLienHe toEntity(LichSuLienHeAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        LichSuLienHe entity = new LichSuLienHe();
        entity.setNguoiLienHe(dto.getNguoiLienHe());
        entity.setEmail(dto.getEmail());
        entity.setSoDienThoai(dto.getSoDienThoai());

        // Nếu DTO không gửi ngayLienHe, mặc định lấy thời gian hiện tại
        entity.setNgayLienHe(dto.getNgayLienHe() != null ? dto.getNgayLienHe() : LocalDateTime.now());

        // lienHeId sẽ được gán đối tượng LienHe tương ứng trong Service thông qua
        // Repository

        return entity;
    }

    /**
     * Cập nhật thông tin Entity hiện có từ DTO
     */
    public void updateEntity(LichSuLienHe entity, LichSuLienHeAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setNguoiLienHe(dto.getNguoiLienHe());
        entity.setEmail(dto.getEmail());
        entity.setSoDienThoai(dto.getSoDienThoai());

        // Cập nhật ngày liên hệ nếu cần, hoặc giữ nguyên ngày cũ tùy logic nghiệp vụ
        if (dto.getNgayLienHe() != null) {
            entity.setNgayLienHe(dto.getNgayLienHe());
        }
    }

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     */
    public LichSuLienHeAdminResponseDTO toResponseDTO(LichSuLienHe entity) {
        if (entity == null) {
            return null;
        }

        LichSuLienHeAdminResponseDTO dto = new LichSuLienHeAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setNguoiLienHe(entity.getNguoiLienHe());
        dto.setEmail(entity.getEmail());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setNgayLienHe(entity.getNgayLienHe());

        // Ánh xạ ID từ thực thể quan hệ (LienHe)
        if (entity.getLienHe() != null) {
            dto.setLienHeId(entity.getLienHe().getId());
        }

        return dto;
    }
}