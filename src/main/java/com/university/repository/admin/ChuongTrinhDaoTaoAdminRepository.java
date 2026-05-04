package com.university.repository.admin;

import com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO.ChuongTrinhDaoTaoView;
import com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO;
import com.university.entity.ChuongTrinhDaoTao;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChuongTrinhDaoTaoAdminRepository extends JpaRepository<ChuongTrinhDaoTao, UUID> {
    List<ChuongTrinhDaoTaoView> findAllProjectedBy();

    List<ChuongTrinhDaoTaoView> findAllProjectedByNganh_Id(UUID nganhId);

    @Query("""
             SELECT new com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO(
                 c.id,
                 m.id,
                 m.maMonHoc,
                 m.tenMonHoc,
                 m.soTinChi,
                 m.moTa,
                 n.maNganh
             )
             FROM ChuongTrinhDaoTao c
             JOIN c.nganh n
             JOIN c.monHoc m
             WHERE n.id= :keyword
            """)
    List<ChuongTrinhDaoTaoAdminResponseDTO> findAllMonHocByNganhId(@Param("keyword") String keyword);

    void deleteAllByIdIn(List<UUID> ids);
}
