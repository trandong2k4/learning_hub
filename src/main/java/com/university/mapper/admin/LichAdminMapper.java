package com.university.mapper.admin;

import com.university.dto.request.admin.LichAdminRequestDTO;
import com.university.dto.response.admin.LichAdminResponseDTO;
import com.university.entity.Lich;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LichAdminMapper {

    public Lich toEntity(LichAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Lich entity = new Lich();
        entity.setNgayHoc(dto.getNgayHoc());
        entity.setGhiChu(dto.getGhiChu());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    public void updateEntity(Lich entity, LichAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNgayHoc(dto.getNgayHoc());
        entity.setGhiChu(dto.getGhiChu());
        entity.setUpdatedAt(LocalDateTime.now());
    }

    public LichAdminResponseDTO toResponseDTO(Lich entity) {
        if (entity == null) {
            return null;
        }

        LichAdminResponseDTO dto = new LichAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setNgayHoc(entity.getNgayHoc());
        dto.setGhiChu(entity.getGhiChu());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getGioHoc() != null) {
            dto.setGioHocId(entity.getGioHoc().getId());
            dto.setGioHoc(new LichAdminResponseDTO.GioHocInfo(
                    entity.getGioHoc().getId(),
                    entity.getGioHoc().getMaGioHoc(),
                    entity.getGioHoc().getTenGioHoc(),
                    entity.getGioHoc().getThoiGianBatDau() != null ? entity.getGioHoc().getThoiGianBatDau().toString() : null,
                    entity.getGioHoc().getThoiGianKetThuc() != null ? entity.getGioHoc().getThoiGianKetThuc().toString() : null));
        }

        if (entity.getPhong() != null) {
            dto.setPhongId(entity.getPhong().getId());
            dto.setPhong(new LichAdminResponseDTO.PhongInfo(
                    entity.getPhong().getId(),
                    entity.getPhong().getMaPhong(),
                    entity.getPhong().getTenPhong()));
        }

        if (entity.getLopHocPhan() != null) {
            dto.setLopHocPhanId(entity.getLopHocPhan().getId());
            dto.setLopHocPhan(new LichAdminResponseDTO.LopHocPhanInfo(
                    entity.getLopHocPhan().getId(),
                    entity.getLopHocPhan().getMaLopHocPhan(),
                    entity.getLopHocPhan().getMonHoc() != null ? entity.getLopHocPhan().getMonHoc().getTenMonHoc() : null,
                    entity.getLopHocPhan().getHocKi() != null ? entity.getLopHocPhan().getHocKi().getTenHocKi() : null));
        }

        return dto;
    }
}
