package com.university.mapper.admin;

import com.university.dto.request.admin.GiangDayAdminRequestDTO;
import com.university.dto.response.admin.GiangDayAdminResponseDTO;
import com.university.entity.GiangDay;
import org.springframework.stereotype.Component;

@Component
public class GiangDayAdminMapper {

    /**
     * Chuyển từ Request DTO sang Entity để lưu mới
     */
    public GiangDay toEntity(GiangDayAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        GiangDay giangDay = new GiangDay();
        giangDay.setVaiTro(dto.getVaiTro());

        // nhanVienId và lopHocPhanId sẽ được gán đối tượng thực thể trong Service
        // Ví dụ:
        // giangDay.setNhanVien(nhanVienRepository.findById(dto.getNhanVienId()).orElseThrow());

        return giangDay;
    }

    /**
     * Cập nhật Entity hiện tại từ dữ liệu trong Request DTO
     */
    public void updateEntity(GiangDay giangDay, GiangDayAdminRequestDTO dto) {
        if (dto == null || giangDay == null) {
            return;
        }

        giangDay.setVaiTro(dto.getVaiTro());

        // Việc cập nhật lại nhân viên giảng dạy hoặc lớp học phần nên thực hiện ở
        // Service
        // giangDay.setUpdatedAt(LocalDateTime.now()); // Nếu entity có trường này
    }

    /**
     * Chuyển từ Entity sang Response DTO để trả về cho Client
     */
    public GiangDayAdminResponseDTO toResponseDTO(GiangDay entity) {
        if (entity == null) {
            return null;
        }

        GiangDayAdminResponseDTO dto = new GiangDayAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setVaiTro(entity.getVaiTro());

        // Ánh xạ ID từ các thực thể quan hệ (Relation)
        if (entity.getNhanVien() != null) {
            dto.setNhanVienId(entity.getNhanVien().getId());
        }

        if (entity.getLopHocPhan() != null) {
            dto.setLopHocPhanId(entity.getLopHocPhan().getId());
        }

        return dto;
    }
}