package com.university.mapper.admin;

import com.university.dto.request.admin.DangKyTinChiAdminRequestDTO;
import com.university.dto.response.admin.DangKyTinChiAdminResponseDTO;
import com.university.entity.DangKyTinChi;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DangKyTinChiAdminMapper {

    public DangKyTinChi toEntity(DangKyTinChiAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        DangKyTinChi dangKyTinChi = new DangKyTinChi();

        // createdAt lấy từ DTO, nếu DTO không gửi lên thì có thể dùng
        // LocalDateTime.now()
        dangKyTinChi.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());

        // Không set LopHocPhan và HocVien ở đây — sẽ gán riêng từ Repository trong
        // Service
        // Ví dụ trong Service:
        // entity.setLopHocPhan(lopHocPhanRepository.findById(dto.getLopHocPhanId()).get());

        return dangKyTinChi;
    }

    public void updateEntity(DangKyTinChi dangKyTinChi, DangKyTinChiAdminRequestDTO dto) {
        if (dto == null || dangKyTinChi == null) {
            return;
        }

        // createdAt thường không cập nhật lại khi update,
        // nhưng nếu cần bạn có thể dùng: dangKyTinChi.setCreatedAt(dto.getCreatedAt());

        // Logic cập nhật các quan hệ (LopHocPhan, HocVien) sẽ được thực hiện ở Service
    }

    public DangKyTinChiAdminResponseDTO toResponseDTO(DangKyTinChi entity) {
        if (entity == null) {
            return null;
        }

        DangKyTinChiAdminResponseDTO dto = new DangKyTinChiAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());

        // Ánh xạ ID từ các thực thể quan hệ sang DTO
        if (entity.getLopHocPhan() != null) {
            dto.setLopHocPhanId(entity.getLopHocPhan().getId());
        }

        if (entity.getHocVien() != null) {
            dto.setHocVienId(entity.getHocVien().getId());
        }

        return dto;
    }
}