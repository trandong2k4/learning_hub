package com.university.mapper.admin;

import com.university.dto.request.admin.LopHocPhanAdminRequestDTO;
import com.university.dto.response.admin.LopHocPhanAdminResponseDTO;
import com.university.entity.LopHocPhan;
import org.springframework.stereotype.Component;

@Component
public class LopHocPhanAdminMapper {

    public LopHocPhan toEntity(LopHocPhanAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        LopHocPhan entity = new LopHocPhan();
        entity.setMaLopHocPhan(dto.getMaLopHocPhan());
        entity.setSoLuongToiDa(dto.getSoLuongToiDa());
        entity.setTrangThai(dto.getTrangThai());
        entity.setHanDangKy(dto.getHanDangKy());
        entity.setHanHuy(dto.getHanHuy());

        return entity;
    }

    public void updateEntity(LopHocPhan entity, LopHocPhanAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setMaLopHocPhan(dto.getMaLopHocPhan());
        entity.setSoLuongToiDa(dto.getSoLuongToiDa());
        entity.setTrangThai(dto.getTrangThai());
        entity.setHanDangKy(dto.getHanDangKy());
        entity.setHanHuy(dto.getHanHuy());
    }

    public LopHocPhanAdminResponseDTO toResponseDTO(LopHocPhan entity) {
        if (entity == null) {
            return null;
        }

        LopHocPhanAdminResponseDTO dto = new LopHocPhanAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setMaLopHocPhan(entity.getMaLopHocPhan());
        dto.setSoLuongToiDa(entity.getSoLuongToiDa());
        dto.setSoLuongDaDangKy((long) entity.getDDangKyTinChis().size());
        dto.setTrangThai(entity.getTrangThai());
        dto.setHanDangKy(entity.getHanDangKy());
        dto.setHanHuy(entity.getHanHuy());

        if (entity.getHocKi() != null) {
            dto.setHocKiId(entity.getHocKi().getId());
            dto.setMaHocKi(entity.getHocKi().getMaHocKi());
            dto.setTenHocKi(entity.getHocKi().getTenHocKi());
        }

        if (entity.getMonHoc() != null) {
            dto.setMonHocId(entity.getMonHoc().getId());
            dto.setMaMonHoc(entity.getMonHoc().getMaMonHoc());
            dto.setTenMonHoc(entity.getMonHoc().getTenMonHoc());
            dto.setSoTinChi(entity.getMonHoc().getSoTinChi());
        }

        return dto;
    }
}
