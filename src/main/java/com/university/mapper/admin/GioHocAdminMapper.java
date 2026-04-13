package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.GioHocAdminRequestDTO;
import com.university.dto.response.admin.GioHocAdminResponseDTO;
import com.university.entity.GioHoc;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GioHocAdminMapper {

    public GioHoc toEntity(GioHocAdminRequestDTO dto) {
        GioHoc gioHoc = new GioHoc();
        gioHoc.setMaGioHoc(dto.getMaGioHoc());
        gioHoc.setTenGioHoc(dto.getTenGioHoc());
        gioHoc.setThoiGianBatDau(dto.getThoiGianBatDau());
        gioHoc.setThoiGianKetThuc(dto.getThoiGianKetThuc());
        return gioHoc;
    }

    public GioHocAdminResponseDTO updateEntity(GioHoc gioHoc, GioHocAdminRequestDTO dto) {
        gioHoc.setMaGioHoc(dto.getMaGioHoc());
        gioHoc.setTenGioHoc(dto.getTenGioHoc());
        gioHoc.setThoiGianBatDau(dto.getThoiGianBatDau());
        gioHoc.setThoiGianKetThuc(dto.getThoiGianKetThuc());
        return toResponseDTO(gioHoc);
    }

    public GioHocAdminResponseDTO toResponseDTO(GioHoc entity) {
        GioHocAdminResponseDTO gioHoc = new GioHocAdminResponseDTO();
        gioHoc.setMaGioHoc(entity.getMaGioHoc());
        gioHoc.setTenGioHoc(entity.getTenGioHoc());
        gioHoc.setThoiGianBatDau(entity.getThoiGianBatDau());
        gioHoc.setThoiGianKetThuc(entity.getThoiGianKetThuc());
        return gioHoc;
    }
}
