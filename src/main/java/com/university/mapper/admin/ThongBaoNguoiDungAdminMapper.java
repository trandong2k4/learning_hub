package com.university.mapper.admin;

import com.university.dto.request.admin.ThongBaoNguoiDungAdminRequestDTO;
import com.university.dto.response.admin.ThongBaoNguoiDungAdminResponseDTO;
import com.university.entity.ThongBaoNguoiDung;
import org.springframework.stereotype.Component;

@Component
public class ThongBaoNguoiDungAdminMapper {

    public ThongBaoNguoiDung toEntity(ThongBaoNguoiDungAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ThongBaoNguoiDung entity = new ThongBaoNguoiDung();
        entity.setDaNhan(dto.getDaNhan() != null ? dto.getDaNhan() : false);
        return entity;
    }

    public void updateEntity(ThongBaoNguoiDung entity, ThongBaoNguoiDungAdminRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setDaNhan(dto.getDaNhan() != null ? dto.getDaNhan() : false);
    }

    public ThongBaoNguoiDungAdminResponseDTO toResponseDTO(ThongBaoNguoiDung entity) {
        if (entity == null) {
            return null;
        }

        ThongBaoNguoiDungAdminResponseDTO dto = new ThongBaoNguoiDungAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setDaNhan(entity.getDaNhan());

        if (entity.getUsers() != null) {
            dto.setUserId(entity.getUsers().getId());
            dto.setUserName(entity.getUsers().getUsername());
            dto.setHoTen(entity.getUsers().getHoTen());
        }

        if (entity.getThongBao() != null) {
            dto.setThongBaoId(entity.getThongBao().getId());
            dto.setTieuDe(entity.getThongBao().getTieuDe());
        }

        return dto;
    }
}
