package com.university.mapper.admin;

import com.university.dto.request.admin.ThongBaoAdminRequestDTO;
import com.university.dto.response.admin.ThongBaoAdminResponseDTO;
import com.university.entity.ThongBao;
import org.springframework.stereotype.Component;

@Component
public class ThongBaoAdminMapper {

    public ThongBao toEntity(ThongBaoAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ThongBao entity = new ThongBao();
        entity.setTieuDe(dto.getTieuDe());
        entity.setNoiDung(dto.getNoiDung());
        entity.setFileThongBao(dto.getFileThongBao());
        entity.setLoaiThongBao(dto.getLoaiThongBao());
        return entity;
    }

    public void updateEntity(ThongBao entity, ThongBaoAdminRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setTieuDe(dto.getTieuDe());
        entity.setNoiDung(dto.getNoiDung());
        entity.setFileThongBao(dto.getFileThongBao());
        entity.setLoaiThongBao(dto.getLoaiThongBao());
    }

    public ThongBaoAdminResponseDTO toResponseDTO(ThongBao entity) {
        if (entity == null) {
            return null;
        }

        ThongBaoAdminResponseDTO dto = new ThongBaoAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setTieuDe(entity.getTieuDe());
        dto.setNoiDung(entity.getNoiDung());
        dto.setFileThongBao(entity.getFileThongBao());
        dto.setLoaiThongBao(entity.getLoaiThongBao());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getUsers() != null) {
            dto.setUsersId(entity.getUsers().getId());
            dto.setUserName(entity.getUsers().getUsername());
            dto.setHoTen(entity.getUsers().getHoTen());
        }

        dto.setSoNguoiNhan((long) entity.getNguoiNhanList().size());
        dto.setSoNguoiDaNhan(entity.getNguoiNhanList().stream()
                .filter(item -> Boolean.TRUE.equals(item.getDaNhan()))
                .count());
        return dto;
    }
}
