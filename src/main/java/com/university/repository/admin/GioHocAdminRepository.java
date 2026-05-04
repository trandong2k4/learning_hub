package com.university.repository.admin;

import com.university.dto.response.admin.GioHocAdminResponseDTO;
import com.university.entity.GioHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GioHocAdminRepository extends JpaRepository<GioHoc, UUID> {

    List<GioHocAdminResponseDTO.GioHocView> findAllProjectedBy();

    boolean existsByMaGioHoc(String maGioHoc);

    @Query("""
             SELECT new com.university.dto.response.admin.GioHocAdminResponseDTO(
                 g.id,
                 g.maGioHoc,
                 g.tenGioHoc,
                 g.thoiGianBatDau,
                 g.thoiGianKetThuc
             )
             FROM GioHoc g
            """)
    List<GioHocAdminResponseDTO> findAllDTO();

    @Query("""
             SELECT new com.university.dto.response.admin.GioHocAdminResponseDTO(
                 g.id,
                 g.maGioHoc,
                 g.tenGioHoc,
                 g.thoiGianBatDau,
                 g.thoiGianKetThuc
             )
             FROM GioHoc g
             WHERE g.id = :id
            """)
    GioHocAdminResponseDTO findDTOById(@Param("id") UUID id);

    @Query("""
             SELECT new com.university.dto.response.admin.GioHocAdminResponseDTO(
                 g.id,
                 g.maGioHoc,
                 g.tenGioHoc,
                 g.thoiGianBatDau,
                 g.thoiGianKetThuc
             )
             FROM GioHoc g
             WHERE LOWER(g.tenGioHoc) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<GioHocAdminResponseDTO> searchByTenGioHoc(@Param("keyword") String keyword);

    void deleteAllByIdIn(List<UUID> ids);
}