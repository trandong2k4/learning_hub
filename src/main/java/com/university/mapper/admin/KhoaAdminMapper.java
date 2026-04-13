package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.KhoaAminRequestDTO;
import com.university.dto.response.admin.KhoaAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.entity.Truong;

@Component
public class KhoaAdminMapper {

    // Chuyển từ DTO sang Entity
    public Khoa toEntity(KhoaAminRequestDTO dto, Truong truong) {
        Khoa khoa = new Khoa();
        khoa.setMaKhoa(dto.getMaKhoa());
        khoa.setTenKhoa(dto.getTenKhoa());
        khoa.setDiaChi(dto.getDiaChi());
        khoa.setMoTa(dto.getMoTa());
        khoa.setTruong(truong);
        return khoa;
    }

    public KhoaAdminResponseDTO upDateEntity(Khoa k, KhoaAminRequestDTO dto, Truong truong) {
        k.setMaKhoa(dto.getMaKhoa());
        k.setTenKhoa(dto.getTenKhoa());
        k.setDiaChi(dto.getDiaChi());
        k.setMoTa(dto.getMoTa());
        k.setTruong(truong);
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
        return dto;
    }
}
