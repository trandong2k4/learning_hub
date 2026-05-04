package com.university.mapper.admin;

import com.university.dto.request.admin.ThongBaoNguoiDungAdminRequestDTO;
import com.university.dto.response.admin.ThongBaoNguoiDungAdminResponseDTO;
import com.university.entity.ThongBaoNguoiDung;
import org.springframework.stereotype.Component;

@Component
public class ThongBaoNguoiDungAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity để lưu mới
     */
    public ThongBaoNguoiDung toEntity(ThongBaoNguoiDungAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ThongBaoNguoiDung entity = new ThongBaoNguoiDung();
        // Mặc định trạng thái daNhan nếu DTO không truyền vào
        entity.setDaNhan(dto.getDaNhan() != null ? dto.getDaNhan() : false);

        // userId và thongBaoId sẽ được gán đối tượng Entity tương ứng trong Service
        // Ví dụ:
        // entity.setUsers(usersRepository.findById(dto.getUserId()).orElseThrow());

        return entity;
    }

    /**
     * Cập nhật thông tin Entity hiện có từ DTO
     */
    public void updateEntity(ThongBaoNguoiDung entity, ThongBaoNguoiDungAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getDaNhan() != null) {
            entity.setDaNhan(dto.getDaNhan());
        }

        // Việc thay đổi User hoặc Thông báo nhận được thường ít khi xảy ra sau khi tạo,
        // nhưng nếu cần bạn hãy xử lý tìm kiếm và gán lại object trong Service.
    }

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     */
    public ThongBaoNguoiDungAdminResponseDTO toResponseDTO(ThongBaoNguoiDung entity) {
        if (entity == null) {
            return null;
        }

        ThongBaoNguoiDungAdminResponseDTO dto = new ThongBaoNguoiDungAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setDaNhan(entity.getDaNhan());

        // Ánh xạ ID từ các thực thể quan hệ (Relation)
        if (entity.getUsers() != null) {
            dto.setUserId(entity.getUsers().getId());
        }

        if (entity.getThongBao() != null) {
            dto.setThongBaoId(entity.getThongBao().getId());
        }

        return dto;
    }
}