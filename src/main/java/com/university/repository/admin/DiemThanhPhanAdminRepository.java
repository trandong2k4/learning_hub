package com.university.repository.admin;

import com.university.dto.response.admin.DiemThanhPhanAdminResponseDTO;
import com.university.entity.DiemThanhPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiemThanhPhanAdminRepository extends JpaRepository<DiemThanhPhan, UUID> {
    @Query("""
                SELECT
                    d.id AS id,
                    d.diemSo AS diemSo,
                    d.lanNhap AS lanNhap,
                    d.ghiChu AS ghiChu,
                    d.updatedAt AS updatedAt,
                    d.dangKyTinChi.id AS dangKyTinChiId,
                    d.cotDiem.id AS cotDiemId
                FROM DiemThanhPhan d
            """)
    List<DiemThanhPhanAdminResponseDTO.DiemThanhPhanView> findAllView();

    void deleteAllByIdIn(List<UUID> ids);
}
