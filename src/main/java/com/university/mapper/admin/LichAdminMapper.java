package com.university.mapper.admin;

import com.university.dto.request.admin.LichAdminRequestDTO;
import com.university.dto.response.admin.LichAdminResponseDTO;
import com.university.entity.Lich;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LichAdminMapper {

    public Lich toEntity(LichAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Lich entity = new Lich();
        entity.setNgayHoc(dto.getNgayHoc());
        entity.setGhiChu(dto.getGhiChu());

        // Thiết lập thời gian khởi tạo
        entity.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        entity.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());

        // Các quan hệ: gioHocId, phongId, lopHocPhanId sẽ được gán Object trong Service

        return entity;
    }

    /**
     * Cập nhật Entity hiện tại từ dữ liệu trong Request DTO
     */
    public void updateEntity(Lich entity, LichAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setNgayHoc(dto.getNgayHoc());
        entity.setGhiChu(dto.getGhiChu());

        // Luôn cập nhật thời gian sửa đổi mới nhất
        entity.setUpdatedAt(LocalDateTime.now());

        // Lưu ý: Việc thay đổi Phòng, Giờ hoặc Lớp học phần nên thực hiện tìm kiếm
        // Object ở Service
    }

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     */
    public LichAdminResponseDTO toResponseDTO(Lich entity) {
        if (entity == null) {
            return null;
        }

        LichAdminResponseDTO dto = new LichAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setNgayHoc(entity.getNgayHoc());
        dto.setGhiChu(entity.getGhiChu());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Ánh xạ ID từ các thực thể quan hệ
        if (entity.getGioHoc() != null) {
            dto.setGioHocId(entity.getGioHoc().getId());
        }

        if (entity.getPhong() != null) {
            dto.setPhongId(entity.getPhong().getId());
        }

        if (entity.getLopHocPhan() != null) {
            dto.setLopHocPhanId(entity.getLopHocPhan().getId());
        }

        return dto;
    }
}