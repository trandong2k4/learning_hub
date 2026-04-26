package com.university.repository.admin;

import com.university.dto.response.admin.DanhGiaGiangVienAdminResponseDTO;
import com.university.entity.DanhGiaGiangVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DanhGiaGiangVienAdminRepository extends JpaRepository<DanhGiaGiangVien, UUID> {
    @Query("""
                SELECT
                    d.id AS id,
                    d.diemDanhGia AS diemDanhGia,
                    d.nhanXet AS nhanXet,
                    d.nhanVien.id AS nhanVienId,
                    d.lopHocPhan.id AS lopHocPhanId
                FROM DanhGiaGiangVien d
            """)
    List<DanhGiaGiangVienAdminResponseDTO.DanhGiaGiangVienView> findAllView();
}
