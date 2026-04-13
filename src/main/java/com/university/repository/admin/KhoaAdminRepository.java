package com.university.repository.admin;

import com.university.dto.response.admin.KhoaAdminResponseDTO;
import com.university.entity.Khoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface KhoaAdminRepository extends JpaRepository<Khoa, UUID> {

    boolean existsByMaKhoa(String maKhoa);

    @Query("""
             SELECT new com.university.dto.response.admin.KhoaAdminResponseDTO(
                 k.id,
                 k.maKhoa,
                 k.tenKhoa,
                 k.diaChi,
                 k.moTa,
                 t.id,
                 t.tenTruong
             )
             FROM Khoa k
             JOIN k.truong t
            """)
    List<KhoaAdminResponseDTO> findAllKhoaDTO();

    @Query("""
             SELECT new com.university.dto.response.admin.KhoaAdminResponseDTO(
                 k.id,
                 k.maKhoa,
                 k.tenKhoa,
                 k.diaChi,
                 k.moTa,
                 t.id,
                 t.tenTruong
             )
             FROM Khoa k
             JOIN k.truong t
             WHERE k.id = :khoaId
            """)
    KhoaAdminResponseDTO findByIdKhoaDTO(@Param("khoaId") UUID khoaId);

    @Query("""
             SELECT new com.university.dto.response.admin.KhoaAdminResponseDTO(
                 k.id,
                 k.maKhoa,
                 k.tenKhoa,
                 k.diaChi,
                 k.moTa,
                 t.id,
                 t.tenTruong
             )
             FROM Khoa k
             JOIN k.truong t
             WHERE LOWER(k.tenKhoa) LIKE LOWER(CONCAT('%', :keyword , '%'))
            """)
    List<KhoaAdminResponseDTO> findByNameKhoaDTO(@Param("keyword") String keyword);

}