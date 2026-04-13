package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.TruongAdminRequestDTO;
import com.university.dto.response.admin.TruongAdminResponseDTO;
import com.university.entity.Truong;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TruongAdminMapper {

    public Truong toEntity(TruongAdminRequestDTO dto) {
        Truong truong = new Truong();
        truong.setMaTruong(dto.getMaTruong());
        truong.setTenTruong(dto.getTenTruong());
        truong.setDiaChi(dto.getDiaChi());
        truong.setMoTa(dto.getMoTa());
        truong.setNgayThanhLap(dto.getNgayThanhLap().atStartOfDay());
        truong.setNguoiDaiDien(dto.getNguoiDaiDien());
        return truong;
    }

    public void updateEntity(Truong truong, TruongAdminRequestDTO dto) {
        truong.setMaTruong(dto.getMaTruong());
        truong.setTenTruong(dto.getTenTruong());
        truong.setDiaChi(dto.getDiaChi());
        truong.setMoTa(dto.getMoTa());
        truong.setNgayThanhLap(dto.getNgayThanhLap().atStartOfDay());
        truong.setNguoiDaiDien(dto.getNguoiDaiDien());
    }

    public TruongAdminResponseDTO toResponseDTO(Truong entity) {
        TruongAdminResponseDTO t = new TruongAdminResponseDTO();
        t.setId(entity.getId());
        t.setMaTruong(entity.getMaTruong());
        t.setTenTruong(entity.getTenTruong());
        t.setDiaChi(entity.getDiaChi());
        t.setMoTa(entity.getMoTa());
        t.setNgayThanhLap(entity.getNgayThanhLap());
        t.setNguoiDaiDien(t.getNguoiDaiDien());
        return t;
    }
}
