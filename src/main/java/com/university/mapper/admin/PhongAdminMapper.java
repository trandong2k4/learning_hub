package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.PhongAdminRequestDTO;
import com.university.dto.response.admin.PhongAdminResponseDTO;
import com.university.entity.Phong;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PhongAdminMapper {

    public Phong toEntity(PhongAdminRequestDTO dto) {
        Phong phong = new Phong();
        phong.setMaPhong(dto.getMaPhong());
        phong.setTenPhong(dto.getTenPhong());
        
        return phong;
    }

    public void updateEntity(Phong phong, PhongAdminRequestDTO dto) {
        phong.setMaPhong(dto.getMaPhong());
        phong.setTenPhong(dto.getTenPhong());
    }

    public PhongAdminResponseDTO toResponseDTO(Phong entity) {
        PhongAdminResponseDTO t = new PhongAdminResponseDTO();
        t.setId(entity.getId());
        t.setMaPhong(entity.getMaPhong());
        t.setTenPhong(entity.getTenPhong());
        return t;
    }
}
