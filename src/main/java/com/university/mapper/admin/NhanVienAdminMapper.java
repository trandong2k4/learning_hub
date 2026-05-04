package com.university.mapper.admin;

import com.university.dto.request.admin.NhanVienAdminRequestDTO;
import com.university.dto.response.admin.NhanVienAdminResponseDTO;
import com.university.entity.NhanVien;
import org.springframework.stereotype.Component;

@Component
public class NhanVienAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity để lưu mới
     */
    public NhanVien toEntity(NhanVienAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        NhanVien entity = new NhanVien();
        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setNgayNhanViec(dto.getNgayNhanViec());
        entity.setNgayNghiViec(dto.getNgayNghiViec());

        // usersId sẽ được gán đối tượng Users trong Service thông qua UsersRepository
        // Ví dụ:
        // entity.setUsers(usersRepository.findById(dto.getUsersId()).orElseThrow());

        return entity;
    }

    /**
     * Cập nhật thông tin Entity hiện có từ DTO
     */
    public void updateEntity(NhanVien entity, NhanVienAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setNgayNhanViec(dto.getNgayNhanViec());
        entity.setNgayNghiViec(dto.getNgayNghiViec());

        // Việc thay đổi tài khoản người dùng (Users) nếu có nên được xử lý ở tầng
        // Service
        // entity.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     */
    public NhanVienAdminResponseDTO toResponseDTO(NhanVien entity) {
        if (entity == null) {
            return null;
        }

        NhanVienAdminResponseDTO dto = new NhanVienAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setMaNhanVien(entity.getMaNhanVien());
        dto.setNgayNhanViec(entity.getNgayNhanViec());
        dto.setNgayNghiViec(entity.getNgayNghiViec());

        // Ánh xạ ID từ thực thể Users sang DTO
        if (entity.getUsers() != null) {
            dto.setUsersId(entity.getUsers().getId());
        }

        return dto;
    }
}