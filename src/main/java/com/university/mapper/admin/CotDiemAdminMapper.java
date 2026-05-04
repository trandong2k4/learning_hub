package com.university.mapper.admin;

import com.university.dto.request.admin.CotDiemAdminRequestDTO;
import com.university.dto.response.admin.CotDiemAdminResponseDTO;
import com.university.entity.CotDiem;
import org.springframework.stereotype.Component;

@Component
public class CotDiemAdminMapper {

    public CotDiem toEntity(CotDiemAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        CotDiem cotDiem = new CotDiem();
        cotDiem.setTenCotDiem(dto.getTenCotDiem());
        cotDiem.setTiTrong(dto.getTiTrong());
        cotDiem.setLoai(dto.getLoai());
        cotDiem.setThuTuHienThi(dto.getThuTuHienThi());

        // Không set LopHocPhan ở đây — sẽ gán từ LopHocPhanRepository trong Service
        // Không set createdAt/updatedAt ở đây — sẽ set trong service hoặc nhờ JPA
        // Auditing

        return cotDiem;
    }

    public void updateEntity(CotDiem cotDiem, CotDiemAdminRequestDTO dto) {
        if (dto == null || cotDiem == null) {
            return;
        }

        cotDiem.setTenCotDiem(dto.getTenCotDiem());
        cotDiem.setTiTrong(dto.getTiTrong());
        cotDiem.setLoai(dto.getLoai());
        cotDiem.setThuTuHienThi(dto.getThuTuHienThi());

        // Cập nhật thời gian sửa đổi (nếu entity có trường này)
        // cotDiem.setUpdatedAt(LocalDateTime.now());

        // Lưu ý: Nếu thay đổi lớp học phần, việc này nên được xử lý ở Service
    }

    public CotDiemAdminResponseDTO toResponseDTO(CotDiem entity) {
        if (entity == null) {
            return null;
        }

        CotDiemAdminResponseDTO dto = new CotDiemAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setTenCotDiem(entity.getTenCotDiem());
        dto.setTiTrong(entity.getTiTrong());
        dto.setLoai(entity.getLoai());
        dto.setThuTuHienThi(entity.getThuTuHienThi());

        // Nếu trong Response DTO của bạn sau này có thêm field lopHocPhanId,
        // hãy bổ sung: dto.setLopHocPhanId(entity.getLopHocPhan().getId());

        return dto;
    }
}
