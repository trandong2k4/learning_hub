package com.university.mapper.admin;

import com.university.dto.request.admin.HocVienAdminRequestDTO;
import com.university.dto.response.admin.HocVienAdminResponseDTO;
import com.university.entity.HocVien;
import com.university.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class HocVienAdminMapper {

    public HocVien toEntity(HocVienAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        HocVien hocVien = new HocVien();
        hocVien.setMaHocVien(dto.getMaHocVien());
        hocVien.setNgayNhapHoc(dto.getNgayNhapHoc());
        hocVien.setNgayTotNghiep(dto.getNgayTotNghiep());
        return hocVien;
    }

    public void updateEntity(HocVien hocVien, HocVienAdminRequestDTO dto) {
        if (dto == null || hocVien == null) {
            return;
        }

        hocVien.setMaHocVien(dto.getMaHocVien());
        hocVien.setNgayNhapHoc(dto.getNgayNhapHoc());
        hocVien.setNgayTotNghiep(dto.getNgayTotNghiep());
    }

    public HocVienAdminResponseDTO toResponseDTO(HocVien entity, Users users) {
        if (entity == null) {
            return null;
        }

        HocVienAdminResponseDTO dto = new HocVienAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setMaHocVien(entity.getMaHocVien());
        dto.setNgayNhapHoc(entity.getNgayNhapHoc());
        dto.setNgayTotNghiep(entity.getNgayTotNghiep());

        if (entity.getNganh() != null) {
            dto.setNganhId(entity.getNganh().getId());
            dto.setTenNganh(entity.getNganh().getTenNganh());
        }

        if (users != null) {
            dto.setUsersId(users.getId());
            dto.setUserName(users.getUsername());
            dto.setEmail(users.getEmail());
            dto.setCccd(users.getCccd());
            dto.setDiaChi(users.getDiaChi());
            dto.setSoDienThoai(users.getSoDienThoai());
            dto.setNgaySinh(users.getNgaySinh());
            dto.setTenNhanVien(users.getHoTen());
            dto.setGioiTinh(users.getGioiTinh());
            dto.setTrangThai(users.isTrangThai());
            dto.setGhiChu(users.getGhiChu());
        }

        return dto;
    }
}
