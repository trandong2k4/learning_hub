package com.university.mapper.admin;

import com.university.dto.request.admin.ThongBaoAdminRequestDTO;
import com.university.dto.response.admin.ThongBaoAdminResponseDTO;
import com.university.entity.ThongBao;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ThongBaoAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity để lưu mới
     */
    public ThongBao toEntity(ThongBaoAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ThongBao entity = new ThongBao();
        entity.setTieuDe(dto.getTieuDe());
        entity.setNoiDung(dto.getNoiDung());
        entity.setFileThongBao(dto.getFileThongBao());
        entity.setLoaiThongBao(dto.getLoaiThongBao());

        // Thiết lập thời gian tạo
        entity.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());

        // Gán trực tiếp đối tượng Users từ DTO (theo cấu trúc DTO hiện tại của bạn)
        entity.setUsers(dto.getUsersId());

        return entity;
    }

    /**
     * Cập nhật thông tin Entity hiện có từ DTO
     */
    public void updateEntity(ThongBao entity, ThongBaoAdminRequestDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTieuDe(dto.getTieuDe());
        entity.setNoiDung(dto.getNoiDung());
        entity.setFileThongBao(dto.getFileThongBao());
        entity.setLoaiThongBao(dto.getLoaiThongBao());

        // Thường không cập nhật lại người tạo (Users) và thời gian tạo ban đầu
        // Nếu có trường updatedAt, bạn có thể set ở đây:
        // entity.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     */
    public ThongBaoAdminResponseDTO toResponseDTO(ThongBao entity) {
        if (entity == null) {
            return null;
        }

        ThongBaoAdminResponseDTO dto = new ThongBaoAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setTieuDe(entity.getTieuDe());
        dto.setNoiDung(entity.getNoiDung());
        dto.setFileThongBao(entity.getFileThongBao());
        dto.setLoaiThongBao(entity.getLoaiThongBao());
        dto.setCreatedAt(entity.getCreatedAt());

        // Trả về đối tượng Users (theo cấu trúc DTO hiện tại của bạn)
        dto.setUsersId(entity.getUsers());

        return dto;
    }
}