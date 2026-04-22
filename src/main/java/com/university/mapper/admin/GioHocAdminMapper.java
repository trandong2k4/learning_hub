package com.university.mapper.admin;

import com.university.dto.request.admin.GioHocAdminRequestDTO;
import com.university.dto.response.admin.GioHocAdminResponseDTO;
import com.university.entity.GioHoc;
import org.springframework.stereotype.Component;

@Component
public class GioHocAdminMapper {

    public GioHoc toEntity(GioHocAdminRequestDTO dto) {
        GioHoc gioHoc = new GioHoc();
        gioHoc.setMaGioHoc(dto.getMaGioHoc());
        gioHoc.setTenGioHoc(dto.getTenGioHoc());
        gioHoc.setThoiGianBatDau(dto.getThoiGianBatDau());
        gioHoc.setThoiGianKetThuc(dto.getThoiGianKetThuc());
        return gioHoc;
    }

    public void updateEntity(GioHoc gioHoc, GioHocAdminRequestDTO dto) {
        gioHoc.setMaGioHoc(dto.getMaGioHoc());
        gioHoc.setTenGioHoc(dto.getTenGioHoc());
        gioHoc.setThoiGianBatDau(dto.getThoiGianBatDau());
        gioHoc.setThoiGianKetThuc(dto.getThoiGianKetThuc());
    }

    public GioHocAdminResponseDTO toResponseDTO(GioHoc entity) {
        GioHocAdminResponseDTO dto = new GioHocAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setMaGioHoc(entity.getMaGioHoc());
        dto.setTenGioHoc(entity.getTenGioHoc());
        dto.setThoiGianBatDau(entity.getThoiGianBatDau());
        dto.setThoiGianKetThuc(entity.getThoiGianKetThuc());
        return dto;
    }
}