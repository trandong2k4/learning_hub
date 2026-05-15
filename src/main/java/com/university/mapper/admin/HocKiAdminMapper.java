package com.university.mapper.admin;

import com.university.dto.request.admin.HocKiAdminRequestDTO;
import com.university.dto.response.admin.HocKiAdminResponseDTO;
import com.university.entity.HocKi;
import org.springframework.stereotype.Component;


@Component
public class HocKiAdminMapper {

    public HocKi toEntity(HocKiAdminRequestDTO dto) {
        if (dto == null)
            return null;

        HocKi entity = new HocKi();
        entity.setMaHocKi(dto.getMaHocKi());
        entity.setTenHocKi(dto.getTenHocKi());
        entity.setNgayBatDau(dto.getNgayBatDau());
        entity.setNgayKetThuc(dto.getNgayKetThuc());

      return entity;
   }

   public void updateEntity(HocKi entity, HocKiAdminRequestDTO dto) {
        if (dto == null || entity == null)
            return;

       entity.setMaHocKi(dto.getMaHocKi());
       entity.setTenHocKi(dto.getTenHocKi());
        entity.setNgayBatDau(dto.getNgayBatDau());
        entity.setNgayKetThuc(dto.getNgayKetThuc());
    }

    public HocKiAdminResponseDTO toResponseDTO(HocKi entity) {
        if (entity == null) return null;
        HocKiAdminResponseDTO dto = new HocKiAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setMaHocKi(entity.getMaHocKi());
        dto.setTenHocKi(entity.getTenHocKi());
        dto.setNgayBatDau(entity.getNgayBatDau());
        dto.setNgayKetThuc(entity.getNgayKetThuc());
        return dto;
    }
}
