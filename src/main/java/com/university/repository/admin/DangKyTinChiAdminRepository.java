package com.university.repository.admin;

import com.university.dto.response.admin.DangKyTinChiAdminResponseDTO;
import com.university.entity.DangKyTinChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DangKyTinChiAdminRepository extends JpaRepository<DangKyTinChi, UUID> {
    @Query("""
                SELECT
                    d.id AS id,
                    d.lopHocPhan.id AS lopHocPhanId,
                    d.hocVien.id AS hocVienId,
                    d.createdAt AS createdAt
                FROM DangKyTinChi d
            """)
    List<DangKyTinChiAdminResponseDTO.DangKyTinChiView> findAllView();

    void deleteAllByIdIn(List<UUID> ids);
}
