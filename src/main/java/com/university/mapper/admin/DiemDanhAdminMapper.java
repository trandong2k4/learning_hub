package com.university.mapper.admin;

import com.university.dto.request.admin.DiemDanhAdminRequestDTO;
import com.university.dto.response.admin.DiemDanhAdminResponseDTO;
import com.university.entity.DiemDanh;
import org.springframework.stereotype.Component;

@Component
public class DiemDanhAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity (Dùng cho tạo mới)
     */
    public DiemDanh toEntity(DiemDanhAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        DiemDanh diemDanh = new DiemDanh();
        diemDanh.setTrangThai(dto.getTrangThai());

        // hocVienId và lichId sẽ được gán đối tượng thực thể trong Service
        // Ví dụ:
        // diemDanh.setHocVien(hocVienRepository.findById(dto.getHocVienId()).get());

        // Các trường audit như createdAt sẽ set ở service hoặc tự động bởi JPA
        return diemDanh;
    }

    /**
     * Cập nhật Entity hiện tại từ DTO
     */
    public void updateEntity(DiemDanh diemDanh, DiemDanhAdminRequestDTO dto) {
        if (dto == null || diemDanh == null) {
            return;
        }

        diemDanh.setTrangThai(dto.getTrangThai());

        // Nếu cần cập nhật thời gian sửa đổi
        // diemDanh.setUpdatedAt(LocalDateTime.now());

        // Lưu ý: Thường ID của học viên và lịch sẽ không thay đổi sau khi đã tạo điểm
        // danh,
        // nhưng nếu cần thay đổi, hãy xử lý tìm kiếm object trong Service.
    }

    /**
     * Chuyển từ Entity sang Response DTO (Dùng để trả kết quả cho Client)
     */
    public DiemDanhAdminResponseDTO toResponseDTO(DiemDanh entity) {
        if (entity == null) {
            return null;
        }

        DiemDanhAdminResponseDTO dto = new DiemDanhAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setTrangThai(entity.getTrangThai());

        // Ánh xạ ID từ các thực thể liên quan
        if (entity.getHocVien() != null) {
            dto.setHocVienId(entity.getHocVien().getId());
        }

        if (entity.getLich() != null) {
            dto.setLichId(entity.getLich().getId());
        }

        // Nếu Entity có các trường thời gian
        // dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}