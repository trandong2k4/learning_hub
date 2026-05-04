package com.university.repository.admin;

import com.university.dto.response.admin.DiemDanhAdminResponseDTO.DiemDanhView;
import com.university.entity.DiemDanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiemDanhAdminRepository extends JpaRepository<DiemDanh, UUID> {
    @Query("""
                SELECT
                    d.id AS id,
                    d.trangThai AS trangThai,
                    d.hocVien.id AS hocVienId,
                    d.lich.id AS lichId
                FROM DiemDanh d
            """)
    List<DiemDanhView> findAllView();

    void deleteAllByIdIn(List<UUID> ids);
}
