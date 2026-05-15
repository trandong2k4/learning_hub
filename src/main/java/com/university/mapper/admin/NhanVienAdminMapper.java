package com.university.mapper.admin;

import com.university.dto.request.admin.NhanVienAdminRequestDTO;
import com.university.dto.response.admin.NhanVienAdminResponseDTO;
import com.university.entity.NhanVien;
import com.university.entity.Users;

import org.springframework.stereotype.Component;

@Component
public class NhanVienAdminMapper {

    public NhanVien toEntity(NhanVienAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        NhanVien entity = new NhanVien();
        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setNgayNhanViec(dto.getNgayNhanViec());
        entity.setNgayNghiViec(dto.getNgayNghiViec());
        return entity;
    }

    public void updateEntity(NhanVien entity, NhanVienAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setNgayNhanViec(dto.getNgayNhanViec());
        entity.setNgayNghiViec(dto.getNgayNghiViec());
    }

    public NhanVienAdminResponseDTO toResponseDTO(NhanVien entity, Users users) {
        if (entity == null) {
            return null;
        }

        NhanVienAdminResponseDTO dto = new NhanVienAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setMaNhanVien(entity.getMaNhanVien());
        dto.setNgayNhanViec(entity.getNgayNhanViec());
        dto.setNgayNghiViec(entity.getNgayNghiViec());

        if (users != null) {
            dto.setUsersId(users.getId());
            dto.setUserName(users.getUsername());
            dto.setEmail(users.getEmail());
            dto.setCccd(users.getCccd());
            dto.setDiaChi(users.getDiaChi());
            dto.setSoDienThoai(users.getSoDienThoai());
            dto.setNgaySinh(users.getNgaySinh() != null ? users.getNgaySinh().toLocalDate() : null);
            dto.setTenNhanVien(users.getHoTen());
            dto.setGioiTinh(users.getGioiTinh());
            dto.setTrangThai(users.isTrangThai());
            dto.setGhiChu(users.getGhiChu());
        }

        return dto;
    }
}
