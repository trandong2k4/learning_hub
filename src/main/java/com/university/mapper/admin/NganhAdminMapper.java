package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.NganhAdminRequestDTO;
import com.university.dto.response.admin.NganhAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.entity.Nganh;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NganhAdminMapper {

    public Nganh toEntity(NganhAdminRequestDTO dto, Khoa khoa) {
        Nganh nganh = new Nganh();
        nganh.setMaNganh(dto.getMaNganh());
        nganh.setTenNganh(dto.getTenNganh());
        nganh.setMoTa(dto.getMoTa());
        nganh.setDanhGia(dto.getDanhGia());
        nganh.setKhoa(khoa);
        return nganh;
    }

    public void upDateEntity(Nganh n, NganhAdminRequestDTO dto, Khoa khoa) {
        n.setMaNganh(dto.getMaNganh());
        n.setTenNganh(dto.getTenNganh());
        n.setMoTa(dto.getMoTa());
        n.setDanhGia(dto.getDanhGia());
        n.setKhoa(khoa);
    }

    public NganhAdminResponseDTO toResponseDTO(Nganh entity) {
        NganhAdminResponseDTO n = new NganhAdminResponseDTO();
        n.setId(entity.getId());
        n.setMaNganh(entity.getMaNganh());
        n.setTenNganh(entity.getTenNganh());
        n.setDanhGia(entity.getDanhGia());
        n.setMoTa(entity.getMoTa());
        return n;
    }
}
