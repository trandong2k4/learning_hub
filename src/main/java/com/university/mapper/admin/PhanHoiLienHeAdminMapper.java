package com.university.mapper.admin;

import com.university.dto.request.admin.PhanHoiLienHeAdminRequestDTO;
import com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO;
import com.university.entity.PhanHoiLienHe;
import com.university.enums.TrangThaiXuLyLienHeEnum;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PhanHoiLienHeAdminMapper {

    public PhanHoiLienHe toEntity(PhanHoiLienHeAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        PhanHoiLienHe entity = new PhanHoiLienHe();
        entity.setHoTen(dto.getHoTen());
        entity.setEmail(dto.getEmail());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setChuDe(dto.getChuDe());
        entity.setNoiDung(dto.getNoiDung());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : TrangThaiXuLyLienHeEnum.CHUA_XU_LY);
        return entity;
    }

    public void updateEntity(PhanHoiLienHe entity, PhanHoiLienHeAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setHoTen(dto.getHoTen());
        entity.setEmail(dto.getEmail());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setChuDe(dto.getChuDe());
        entity.setNoiDung(dto.getNoiDung());
        entity.setGioiTinh(dto.getGioiTinh());
    }

    public PhanHoiLienHeAdminResponseDTO toResponseDTO(PhanHoiLienHe entity) {
        if (entity == null) {
            return null;
        }
        return toResponseDTO(entity, Collections.emptyList());
    }

    public PhanHoiLienHeAdminResponseDTO toResponseDTO(PhanHoiLienHe entity, List<?> lichSuList) {
        if (entity == null) {
            return null;
        }
        PhanHoiLienHeAdminResponseDTO dto = new PhanHoiLienHeAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setHoTen(entity.getHoTen());
        dto.setEmail(entity.getEmail());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setChuDe(entity.getChuDe());
        dto.setNoiDung(entity.getNoiDung());
        dto.setTrangThai(entity.getTrangThai());
        dto.setGioiTinh(entity.getGioiTinh());
        dto.setNguoiXuLy(entity.getNguoiXuLy());
        dto.setNgayTao(entity.getNgayTao());
        dto.setNgayCapNhat(entity.getNgayCapNhat());

        if (lichSuList != null && !lichSuList.isEmpty()) {
            List<Object> list = (List<Object>) lichSuList;
            if (!list.isEmpty()
                    && list.get(0) instanceof com.university.dto.response.admin.LichSuXuLyLienHeAdminResponseDTO) {
                dto.setLichSuXuLys(
                        (List<com.university.dto.response.admin.LichSuXuLyLienHeAdminResponseDTO>) (List<?>) lichSuList);
            }
        }
        return dto;
    }
}
