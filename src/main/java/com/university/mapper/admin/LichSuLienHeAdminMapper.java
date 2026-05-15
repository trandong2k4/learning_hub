package com.university.mapper.admin;

import com.university.dto.request.admin.LichSuLienHeAdminRequestDTO;
import com.university.dto.response.admin.LichSuLienHeAdminResponseDTO;
import com.university.entity.LichSuLienHe;
import com.university.entity.LienHe;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LichSuLienHeAdminMapper {

    public LichSuLienHe toEntity(LichSuLienHeAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        LichSuLienHe entity = new LichSuLienHe();
        entity.setNguoiLienHe(dto.getNguoiLienHe());
        entity.setEmail(dto.getEmail());
        entity.setSoDienThoai(dto.getSoDienThoai());

        // Nếu DTO không gửi ngayLienHe, mặc định lấy thời gian hiện tại
        entity.setNgayLienHe(dto.getNgayLienHe() != null ? dto.getNgayLienHe() : LocalDateTime.now());

        return entity;
    }

    public void updateEntity(LichSuLienHe entity, LichSuLienHeAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setNguoiLienHe(dto.getNguoiLienHe());
        entity.setEmail(dto.getEmail());
        entity.setSoDienThoai(dto.getSoDienThoai());

        if (dto.getNgayLienHe() != null) {
            entity.setNgayLienHe(dto.getNgayLienHe());
        }
    }

    public LichSuLienHeAdminResponseDTO toResponseDTO(LichSuLienHe entity, LienHe lienHe) {
        if (entity == null) {
            return null;
        }

        LichSuLienHeAdminResponseDTO dto = new LichSuLienHeAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setNguoiLienHe(entity.getNguoiLienHe());
        dto.setEmail(entity.getEmail());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setNgayLienHe(entity.getNgayLienHe());

        if (lienHe != null) {
            dto.setLienHeId(lienHe.getId());
            dto.setTenLienHe(lienHe.getTenLienHe());
            dto.setEmailLienHe(lienHe.getEmail());
            dto.setSoDienThoaiLienHe(lienHe.getSoDienThoai());
        }

        return dto;
    }
}
