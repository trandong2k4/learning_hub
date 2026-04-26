package com.university.mapper.admin;

import com.university.dto.request.admin.BaiVietAdminRequestDTO;
import com.university.dto.response.admin.BaiVietAdminResponseDTO;
import com.university.entity.BaiViet;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BaiVietAdminMapper {

    public BaiViet toEntity(BaiVietAdminRequestDTO dto) {
        BaiViet baiViet = new BaiViet();
        baiViet.setTieuDe(dto.getTieuDe());
        baiViet.setNoiDung(dto.getNoiDung());
        baiViet.setNgayDang(dto.getNgayDang());
        baiViet.setTacGia(dto.getTacGia());
        baiViet.setFileDinhKemUrl(dto.getFileDinhKemUrl());
        baiViet.setHinhAnhUrl(dto.getHinhAnhUrl());
        baiViet.setLoaiBaiViet(dto.getLoaiBaiViet());
        baiViet.setTrangThai(dto.getTrangThai());
        // Không set createdAt/updatedAt ở đây — sẽ set trong service
        // Không set users — sẽ gán riêng từ UsersRepository
        return baiViet;
    }

    public void updateEntity(BaiViet baiViet, BaiVietAdminRequestDTO dto) {
        baiViet.setTieuDe(dto.getTieuDe());
        baiViet.setNoiDung(dto.getNoiDung());
        baiViet.setNgayDang(dto.getNgayDang());
        baiViet.setTacGia(dto.getTacGia());
        baiViet.setFileDinhKemUrl(dto.getFileDinhKemUrl());
        baiViet.setHinhAnhUrl(dto.getHinhAnhUrl());
        baiViet.setLoaiBaiViet(dto.getLoaiBaiViet());
        baiViet.setTrangThai(dto.getTrangThai());
        // createdAt không cập nhật lại
        baiViet.setUpdatedAt(LocalDateTime.now()); // cập nhật thời gian sửa
    }

    public BaiVietAdminResponseDTO toResponseDTO(BaiViet entity) {
        BaiVietAdminResponseDTO dto = new BaiVietAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setTieuDe(entity.getTieuDe());
        dto.setNoiDung(entity.getNoiDung());
        dto.setNgayDang(entity.getNgayDang());
        dto.setTacGia(entity.getTacGia());
        dto.setFileDinhKemUrl(entity.getFileDinhKemUrl());
        dto.setHinhAnhUrl(entity.getHinhAnhUrl());
        dto.setLoaiBaiViet(entity.getLoaiBaiViet());
        dto.setTrangThai(entity.getTrangThai());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
