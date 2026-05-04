package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.KhoaAdminRequestDTO;
import com.university.dto.response.admin.KhoaAdminResponseDTO;
import com.university.entity.Khoa;

@Component
public class KhoaAdminMapper {

    // Chuyển từ DTO sang Entity
    public Khoa toEntity(KhoaAdminRequestDTO dto) {
        Khoa khoa = new Khoa();
        khoa.setMaKhoa(dto.getMaKhoa());
        khoa.setTenKhoa(dto.getTenKhoa());
        khoa.setDiaChi(dto.getDiaChi());
        khoa.setMoTa(dto.getMoTa());
        return khoa;
    }

    public KhoaAdminResponseDTO upDateEntity(Khoa k, KhoaAdminRequestDTO dto) {
        k.setMaKhoa(dto.getMaKhoa());
        k.setTenKhoa(dto.getTenKhoa());
        k.setDiaChi(dto.getDiaChi());
        k.setMoTa(dto.getMoTa());
        return toResponseDTO(k);
    }

    // Chuyển từ Entity sang ResponseDTO
    public KhoaAdminResponseDTO toResponseDTO(Khoa k) {
        KhoaAdminResponseDTO dto = new KhoaAdminResponseDTO();
        dto.setId(k.getId());
        dto.setMaKhoa(k.getMaKhoa());
        dto.setTenKhoa(k.getTenKhoa());
        dto.setDiaChi(k.getDiaChi());
        dto.setMoTa(k.getMoTa());
        dto.setTruongId(k.getTruong().getId());
        dto.setTenTruong(k.getTruong().getTenTruong());
        return dto;
    }
}
