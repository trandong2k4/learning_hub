package com.university.mapper.admin;

import com.university.dto.response.admin.LichSuXuLyLienHeAdminResponseDTO;
import com.university.entity.LichSuXuLyLienHe;
import org.springframework.stereotype.Component;

@Component
public class LichSuXuLyLienHeAdminMapper {

    public LichSuXuLyLienHeAdminResponseDTO toResponseDTO(LichSuXuLyLienHe entity) {
        if (entity == null) {
            return null;
        }
        return new LichSuXuLyLienHeAdminResponseDTO(
            entity.getId(),
            entity.getTrangThaiTruoc(),
            entity.getTrangThaiMoi(),
            entity.getNguoiThucHien(),
            entity.getGhiChu(),
            entity.getNoiDungPhanHoi(),
            entity.getThoiGianXuLy()
        );
    }
}
