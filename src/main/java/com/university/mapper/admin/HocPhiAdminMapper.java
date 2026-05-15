package com.university.mapper.admin;

import com.university.dto.response.admin.HocPhiAdminResponseDTO;
import com.university.entity.HocKi;
import com.university.entity.HocPhi;
import com.university.entity.HocVien;
import com.university.entity.ThanhToanHocPhi;

import org.springframework.stereotype.Component;

@Component
public class HocPhiAdminMapper {

    public HocPhiAdminResponseDTO toResponseDTO(HocPhi entity) {
        if (entity == null)
            return null;

        HocVien hocVien = entity.getHocVien();
        HocKi hocKi = entity.getHocKi();
        ThanhToanHocPhi thanhToan = entity.getThanhToanHocPhi();

        HocPhiAdminResponseDTO dto = HocPhiAdminResponseDTO.builder()
                .id(entity.getId())
                .soTien(entity.getSoTien())
                .trangThai(entity.getTrangThai())
                .soTinChi(entity.getSoTinChi())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        if (hocVien != null) {
            dto.setHocVienId(hocVien.getId());
            if (hocVien.getUsers() != null) {
                dto.setHocVienHoTen(hocVien.getUsers().getHoTen());
                dto.setHocVienEmail(hocVien.getUsers().getEmail());
            }
            dto.setHocVienMa(hocVien.getMaHocVien());
        }

        if (hocKi != null) {
            dto.setHocKiId(hocKi.getId());
            dto.setHocKiMa(hocKi.getMaHocKi());
            dto.setHocKiTen(hocKi.getTenHocKi());
        }

        if (thanhToan != null) {
            dto.setThanhToanMaGiaoDich(thanhToan.getMaGiaoDichGateway());
            dto.setThanhToanPhuongThuc(thanhToan.getPhuongThucThanhToan());
            dto.setThanhToanNgay(thanhToan.getNgayThanhToan());
            dto.setSoTienConNo(entity.getSoTien());
        } else {
            dto.setSoTienConNo(entity.getSoTien());
        }

        return dto;
    }
}
