package com.university.repository.admin;

import com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO;
import com.university.entity.ChuongTrinhDaoTao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChuongTrinhDaoTaoAdminRepository extends JpaRepository<ChuongTrinhDaoTao, UUID> {

    @Query("""
            SELECT new com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO(
                c.id,
                n.id,
                n.maNganh,
                n.tenNganh,
                m.id,
                m.maMonHoc,
                m.tenMonHoc,
                m.soTinChi,
                m.moTa
            )
            FROM ChuongTrinhDaoTao c
            JOIN c.nganh n
            JOIN c.monHoc m
            ORDER BY n.maNganh ASC, m.maMonHoc ASC
            """)
    List<ChuongTrinhDaoTaoAdminResponseDTO> findAllDTO();

    @Query("""
            SELECT new com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO(
                c.id,
                n.id,
                n.maNganh,
                n.tenNganh,
                m.id,
                m.maMonHoc,
                m.tenMonHoc,
                m.soTinChi,
                m.moTa
            )
            FROM ChuongTrinhDaoTao c
            JOIN c.nganh n
            JOIN c.monHoc m
            WHERE n.id = :nganhId
            ORDER BY m.maMonHoc ASC
            """)
    List<ChuongTrinhDaoTaoAdminResponseDTO> findAllByNganhIdDTO(@Param("nganhId") UUID nganhId);

    boolean existsByNganh_IdAndMonHoc_Id(UUID nganhId, UUID monHocId);

    boolean existsByNganh_IdAndMonHoc_IdAndIdNot(UUID nganhId, UUID monHocId, UUID id);

    boolean existsByMonHoc_Id(UUID monHocId);

    List<ChuongTrinhDaoTao> findAllByMonHocId(UUID monHocId);

    List<ChuongTrinhDaoTao> findAllByNganhId(UUID nganhId);

    void deleteAllByIdIn(List<UUID> ids);
}
