package com.university.mapper.admin;

import com.university.dto.request.admin.LienHeAdminRequestDTO;
import com.university.dto.response.admin.LienHeAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.entity.LienHe;
import org.springframework.stereotype.Component;

@Component
public class LienHeAdminMapper {

    public LienHe toEntity(LienHeAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        LienHe entity = new LienHe();
        entity.setTenLienHe(dto.getTenLienHe());
        entity.setFanPageUrl(dto.getFanPageUrl());
        entity.setEmail(dto.getEmail());
        entity.setSoDienThoai(dto.getSoDienThoai());
        return entity;
    }

    public void updateEntity(LienHe entity, LienHeAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTenLienHe(dto.getTenLienHe());
        entity.setFanPageUrl(dto.getFanPageUrl());
        entity.setEmail(dto.getEmail());
        entity.setSoDienThoai(dto.getSoDienThoai());
    }

    public LienHeAdminResponseDTO toResponseDTO(LienHe entity, Khoa khoa) {
        if (entity == null) {
            return null;
        }
        LienHeAdminResponseDTO dto = new LienHeAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setTenLienHe(entity.getTenLienHe());
        dto.setFanPageUrl(entity.getFanPageUrl());
        dto.setEmail(entity.getEmail());
        dto.setSoDienThoai(entity.getSoDienThoai());

        if (entity.getKhoa() != null) {
            dto.setKhoaId(khoa.getId());
            dto.setMaKhoa(khoa.getMaKhoa());
            dto.setTenKhoa(khoa.getTenKhoa());
        }
        return dto;
    }
}
