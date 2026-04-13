package com.university.repository.student;

import com.university.dto.response.student.ChuongTrinhDaoTaoResponseDTO;
import com.university.entity.ChuongTrinhDaoTao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChuongTrinhDaoTaoRepository extends JpaRepository<ChuongTrinhDaoTao, UUID> {

    @Query("""
            SELECT new com.university.dto.response.student.ChuongTrinhDaoTaoResponseDTO(
                c.id,
                n.maNganh,
                n.tenNganh,
                m.maMonHoc,
                m.tenMonHoc,
                m.soTinChi,
                m.moTa
            )
               FROM ChuongTrinhDaoTao c
                 JOIN c.nganh n
                 JOIN c.monHoc m
              WHERE c.nganh.id = :nganhId
               """)
    List<ChuongTrinhDaoTaoResponseDTO> findByNganhId(@Param("nganhId") UUID nganhId);

    @Query("""
              SELECT new com.university.dto.response.student.ChuongTrinhDaoTaoResponseDTO(
                c.id,
                n.maNganh,
                n.tenNganh,
                m.maMonHoc,
                m.tenMonHoc,
                m.soTinChi,
                m.moTa
            )
              FROM ChuongTrinhDaoTao c
                JOIN c.nganh n
                JOIN c.monHoc m
                WHERE c.nganh.id = :nganhId
                AND (LOWER(m.maMonHoc) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(m.tenMonHoc) LIKE LOWER(CONCAT('%', :keyword, '%')))
                """)
    List<ChuongTrinhDaoTaoResponseDTO> findByNganhIdAndKeyword(@Param("nganhId") UUID nganhId,
            @Param("keyword") String keyword);
}