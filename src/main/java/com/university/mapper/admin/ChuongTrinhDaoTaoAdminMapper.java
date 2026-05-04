package com.university.mapper.admin;

import com.university.dto.request.admin.ChuongTrinhDaoTaoAdminRequestDTO;
import com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO;
import com.university.entity.ChuongTrinhDaoTao;
import org.springframework.stereotype.Component;

@Component
public class ChuongTrinhDaoTaoAdminMapper {

    public ChuongTrinhDaoTao toEntity(ChuongTrinhDaoTaoAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ChuongTrinhDaoTao entity = new ChuongTrinhDaoTao();
        // Nganh và MonHoc sẽ được gán trong Service thông qua
        // Repository.findById(dto.getNganhId/monHocId)

        // Nếu entity của bạn có các trường audit tự động
        // entity.setCreatedAt(LocalDateTime.now());

        return entity;
    }

    public void updateEntity(ChuongTrinhDaoTao entity, ChuongTrinhDaoTaoAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        // Tương tự toEntity, việc cập nhật quan hệ Nganh/MonHoc nên được thực hiện ở
        // Service
        // để đảm bảo tính toàn vẹn dữ liệu khi tìm kiếm từ database.

        // entity.setUpdatedAt(LocalDateTime.now());
    }

    public ChuongTrinhDaoTaoAdminResponseDTO toResponseDTO(ChuongTrinhDaoTao entity) {
        if (entity == null) {
            return null;
        }
        ChuongTrinhDaoTaoAdminResponseDTO dto = new ChuongTrinhDaoTaoAdminResponseDTO();
        // Giả định Entity ChuongTrinhDaoTao có id và các quan hệ getID
        dto.setId(entity.getId());

        if (entity.getMonHoc() != null) {
            dto.setMonHocId(entity.getMonHoc().getId());
        }
        // Set các thông tin audit nếu cần
        // dto.setCreatedAt(entity.getCreatedAt());
        // dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}