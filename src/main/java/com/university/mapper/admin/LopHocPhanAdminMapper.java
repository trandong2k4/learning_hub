package com.university.mapper.admin;

import com.university.dto.request.admin.LopHocPhanAdminRequestDTO;
import com.university.dto.response.admin.LopHocPhanAdminResponseDTO;
import com.university.entity.LopHocPhan;
import org.springframework.stereotype.Component;

@Component
public class LopHocPhanAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity để lưu mới
     */
    public LopHocPhan toEntity(LopHocPhanAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        LopHocPhan entity = new LopHocPhan();
        entity.setMaLopHocPhan(dto.getMaLopHocPhan());
        entity.setSoLuongToiDa(dto.getSoLuongToiDa());
        entity.setTrangThai(dto.getTrangThai());

        // Nếu DTO truyền thẳng object Entity (như trong code bạn gửi):
        entity.setHocKi(dto.getHocKiId());
        entity.setMonHoc(dto.getMonHocId());

        // Nếu sau này bạn đổi DTO thành UUID, hãy gán các object này trong Service thay
        // vì ở đây

        return entity;
    }

    /**
     * Cập nhật thông tin Entity hiện có từ DTO
     */
    public void updateEntity(LopHocPhan entity, LopHocPhanAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setMaLopHocPhan(dto.getMaLopHocPhan());
        entity.setSoLuongToiDa(dto.getSoLuongToiDa());
        entity.setTrangThai(dto.getTrangThai());

        // Cập nhật quan hệ
        entity.setHocKi(dto.getHocKiId());
        entity.setMonHoc(dto.getMonHocId());

        // entity.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     */
    public LopHocPhanAdminResponseDTO toResponseDTO(LopHocPhan entity) {
        if (entity == null) {
            return null;
        }

        LopHocPhanAdminResponseDTO dto = new LopHocPhanAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setMaLopHocPhan(entity.getMaLopHocPhan());
        dto.setSoLuongToiDa(entity.getSoLuongToiDa());
        dto.setTrangThai(entity.getTrangThai());

        // Trả về object Entity tương ứng theo cấu trúc DTO bạn đã định nghĩa
        dto.setHocKiId(entity.getHocKi());
        dto.setMonHocId(entity.getMonHoc());

        return dto;
    }
}