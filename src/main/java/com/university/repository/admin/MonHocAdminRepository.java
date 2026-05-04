package com.university.repository.admin;

import com.university.dto.response.admin.MonHocAdminResponseDTO;
import com.university.entity.MonHoc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonHocAdminRepository extends JpaRepository<MonHoc, UUID> {

    boolean existsByMaMonHoc(String maMonHoc);

    @Query("SELECT mh.maMonHoc FROM MonHoc mh")
    List<String> findAllMaMonHOc();

    @Query("""
             SELECT new com.university.dto.response.admin.MonHocAdminResponseDTO(
                 mh.id,
                 mh.maMonHoc,
                 mh.tenMonHoc,
                 mh.soTinChi,
                 mh.moTa
             )
             FROM MonHoc mh
            """)
    List<MonHocAdminResponseDTO> FindAllDTO();

    @Query("""
             SELECT new com.university.dto.response.admin.MonHocAdminResponseDTO(
                 mh.id,
                 mh.maMonHoc,
                 mh.tenMonHoc,
                 mh.soTinChi,
                 mh.moTa
             )
             FROM MonHoc mh
             WHERE mh.id = :monhocId
            """)
    MonHocAdminResponseDTO findMonHocById(@Param("monhocId") UUID monhocId);

    @Query("""
            SELECT new com.university.dto.response.admin.MonHocAdminResponseDTO(
                  mh.id,
                  mh.maMonHoc,
                  mh.tenMonHoc,
                  mh.soTinChi,
                  mh.moTa
              )
              FROM MonHoc mh
              WHERE LOWER(mh.tenMonHoc) LIKE LOWER(CONCAT('%',:keyword,'%'))
             """)
    List<MonHocAdminResponseDTO> findMonHocByTen(@Param("keyword") String keyword);

    void deleteAllByIdIn(List<UUID> ids);
}
