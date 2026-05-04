package com.university.mapper.admin;

import com.university.dto.request.admin.HocVienAdminRequestDTO;
import com.university.dto.response.admin.HocVienAdminResponseDTO;
import com.university.entity.HocVien;
import org.springframework.stereotype.Component;

@Component
public class HocVienAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity để lưu mới
     */
    public HocVien toEntity(HocVienAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        HocVien hocVien = new HocVien();
        hocVien.setMaHocVien(dto.getMaHocVien());
        hocVien.setNgayNhapHoc(dto.getNgayNhapHoc());
        hocVien.setNgayTotNghiep(dto.getNgayTotNghiep());

        // nganhId sẽ được gán đối tượng Nganh trong Service bằng NganhRepository

        return hocVien;
    }

    /**
     * Cập nhật thông tin Entity hiện có từ DTO
     */
    public void updateEntity(HocVien hocVien, HocVienAdminRequestDTO dto) {
        if (dto == null || hocVien == null) {
            return;
        }

        hocVien.setMaHocVien(dto.getMaHocVien());
        hocVien.setNgayNhapHoc(dto.getNgayNhapHoc());
        hocVien.setNgayTotNghiep(dto.getNgayTotNghiep());

        // Việc thay đổi ngành (Nganh) nên được xử lý ở Service
        // hocVien.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Chuyển từ Entity sang Response DTO
     */
    public HocVienAdminResponseDTO toResponseDTO(HocVien entity) {
        if (entity == null) {
            return null;
        }

        HocVienAdminResponseDTO dto = new HocVienAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setMaHocVien(entity.getMaHocVien());
        dto.setNgayNhapHoc(entity.getNgayNhapHoc());
        dto.setNgayTotNghiep(entity.getNgayTotNghiep());

        // Ánh xạ ID từ thực thể Nganh sang DTO
        if (entity.getNganh() != null) {
            dto.setNganhId(entity.getNganh().getId());
        }

        return dto;
    }
}