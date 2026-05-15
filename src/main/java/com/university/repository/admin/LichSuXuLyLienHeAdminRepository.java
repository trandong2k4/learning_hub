package com.university.repository.admin;

import com.university.dto.response.admin.LichSuXuLyLienHeAdminResponseDTO;
import com.university.entity.LichSuXuLyLienHe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LichSuXuLyLienHeAdminRepository extends JpaRepository<LichSuXuLyLienHe, UUID> {

    @Query("""
            SELECT new com.university.dto.response.admin.LichSuXuLyLienHeAdminResponseDTO(
                l.id,
                l.trangThaiTruoc,
                l.trangThaiMoi,
                l.nguoiThucHien,
                l.ghiChu,
                l.noiDungPhanHoi,
                l.thoiGianXuLy
            )
            FROM LichSuXuLyLienHe l
            WHERE l.phanHoiLienHe.id = :phanHoiId
            ORDER BY l.thoiGianXuLy DESC
            """)
    List<LichSuXuLyLienHeAdminResponseDTO> findAllByPhanHoiIdDTO(@Param("phanHoiId") UUID phanHoiId);
}
