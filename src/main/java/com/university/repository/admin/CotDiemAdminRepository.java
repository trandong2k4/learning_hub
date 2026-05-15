package com.university.repository.admin;

import com.university.dto.response.admin.CotDiemAdminResponseDTO;
import com.university.entity.CotDiem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CotDiemAdminRepository extends JpaRepository<CotDiem, UUID> {

    @Query("""
                SELECT new com.university.dto.response.admin.CotDiemAdminResponseDTO(
                    c.id,
                    c.tenCotDiem,
                    c.tiTrong,
                    c.loai,
                    c.thuTuHienThi,
                    l.id,
                    l.maLopHocPhan
                )
                FROM CotDiem c
                JOIN c.lopHocPhan l
                ORDER BY l.maLopHocPhan ASC, c.thuTuHienThi ASC, c.tenCotDiem ASC
            """)
    List<CotDiemAdminResponseDTO> findAllDTO();

    @Query("""
                SELECT new com.university.dto.response.admin.CotDiemAdminResponseDTO(
                    c.id,
                    c.tenCotDiem,
                    c.tiTrong,
                    c.loai,
                    c.thuTuHienThi,
                    l.id,
                    l.maLopHocPhan
                )
                FROM CotDiem c
                JOIN c.lopHocPhan l
                WHERE l.id = :lopHocPhanId
                ORDER BY c.thuTuHienThi ASC, c.tenCotDiem ASC
            """)
    List<CotDiemAdminResponseDTO> findAllByLopHocPhanIdDTO(@Param("lopHocPhanId") UUID lopHocPhanId);

    boolean existsByLopHocPhan_IdAndTenCotDiemIgnoreCase(UUID lopHocPhanId, String tenCotDiem);

    boolean existsByLopHocPhan_IdAndTenCotDiemIgnoreCaseAndIdNot(UUID lopHocPhanId, String tenCotDiem, UUID id);

    void deleteAllByIdIn(List<UUID> ids);
}
