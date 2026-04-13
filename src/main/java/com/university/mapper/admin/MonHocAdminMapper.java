package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.MonHocAdminRequestDTO;
import com.university.dto.response.admin.MonHocAdminResponseDTO;
import com.university.entity.MonHoc;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MonHocAdminMapper {

    public MonHoc toEntity(MonHocAdminRequestDTO dto) {
        MonHoc monHoc = new MonHoc();
        monHoc.setMaMonHoc(dto.getMaMonHoc());
        monHoc.setTenMonHoc(dto.getTenMonHoc());
        monHoc.setSoTinChi(dto.getSoTinChi());
        monHoc.setMoTa(dto.getMoTa());
        return monHoc;
    }

    public void updateEntity(MonHoc monHoc, MonHocAdminRequestDTO dto) {
        monHoc.setMaMonHoc(dto.getMaMonHoc());
        monHoc.setTenMonHoc(dto.getTenMonHoc());
        monHoc.setSoTinChi(dto.getSoTinChi());
        monHoc.setMoTa(dto.getMoTa());
    }

    public MonHocAdminResponseDTO toResponseDTO(MonHoc monHoc) {
        MonHocAdminResponseDTO dto = new MonHocAdminResponseDTO();
        dto.setMaMonHoc(monHoc.getMaMonHoc());
        dto.setTenMonHoc(monHoc.getTenMonHoc());
        dto.setSoTinChi(monHoc.getSoTinChi());
        dto.setMoTa(monHoc.getMoTa());
        return dto;
    }
}
