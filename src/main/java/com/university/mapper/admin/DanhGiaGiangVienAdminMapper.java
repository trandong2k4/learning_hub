package com.university.mapper.admin;

import com.university.dto.request.admin.DanhGiaGiangVienAdminRequestDTO;
import com.university.dto.response.admin.DanhGiaGiangVienAdminResponseDTO;
import com.university.entity.DanhGiaGiangVien;
import org.springframework.stereotype.Component;

@Component
public class DanhGiaGiangVienAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity
     */
    public DanhGiaGiangVien toEntity(DanhGiaGiangVienAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        DanhGiaGiangVien entity = new DanhGiaGiangVien();
        entity.setDiemDanhGia(dto.getDiemDanhGia());
        entity.setNhanXet(dto.getNhanXet());

        // nhanVienId và lopHocPhanId sẽ được gán đối tượng Entity tương ứng trong
        // Service
        // entity.setCreatedAt(LocalDateTime.now());

        return entity;
    }

    /**
     * Cập nhật thông tin Entity từ Request DTO
     */
    public void updateEntity(DanhGiaGiangVien entity, DanhGiaGiangVienAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setDiemDanhGia(dto.getDiemDanhGia());
        entity.setNhanXet(dto.getNhanXet());

        // Việc thay đổi Nhân viên hoặc Lớp học phần (nếu có) nên thực hiện ở Service
        // entity.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Chuyển từ Entity sang Response DTO
     */
    public DanhGiaGiangVienAdminResponseDTO toResponseDTO(DanhGiaGiangVien entity) {
        if (entity == null) {
            return null;
        }

        DanhGiaGiangVienAdminResponseDTO dto = new DanhGiaGiangVienAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setDiemDanhGia(entity.getDiemDanhGia());
        dto.setNhanXet(entity.getNhanXet());

        // Lấy ID từ các thực thể quan hệ
        if (entity.getNhanVien() != null) {
            dto.setNhanVienId(entity.getNhanVien().getId());
        }

        if (entity.getLopHocPhan() != null) {
            dto.setLopHocPhanId(entity.getLopHocPhan().getId());
        }

        return dto;
    }
}