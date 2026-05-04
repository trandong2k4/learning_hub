package com.university.repository.admin;

import com.university.dto.response.admin.CotDiemAdminResponseDTO;
import com.university.entity.CotDiem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CotDiemAdminRepository extends JpaRepository<CotDiem, UUID> {

    @Query("""
                SELECT
                    c.id AS id,
                    c.tenCotDiem AS tenCotDiem,
                    c.tiTrong AS tiTrong,
                    c.loai AS loai,
                    c.thuTuHienThi AS thuTuHienThi
                FROM CotDiem c
            """)
    List<CotDiemAdminResponseDTO.CotDiemView> findAllView();

    void deleteAllByIdIn(List<UUID> ids);
}
