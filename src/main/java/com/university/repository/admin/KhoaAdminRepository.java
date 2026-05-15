package com.university.repository.admin;

import com.university.dto.response.admin.KhoaAdminResponseDTO;
import com.university.entity.Khoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface KhoaAdminRepository extends JpaRepository<Khoa, UUID> {

    // boolean existsByTruongId(UUID truongId);

    List<KhoaAdminResponseDTO.KhoaView> findAllProjectedBy();

    KhoaAdminResponseDTO.KhoaView findAllProjectedById(UUID khoaId);

    boolean existsByMaKhoa(String maKhoa);

    Khoa findByMaKhoa(String maKhoa);

    @Query("SELECT k.maKhoa FROM Khoa k")
    List<String> findAllMaKhoa();

    @Query("""
             SELECT new com.university.dto.response.admin.KhoaAdminResponseDTO(
                 k.id,
                 k.maKhoa,
                 k.tenKhoa,
                 k.diaChi,
                 k.moTa
             )
             FROM Khoa k
            """)
    List<KhoaAdminResponseDTO> findAllKhoaDTO();

    @Query("""
             SELECT new com.university.dto.response.admin.KhoaAdminResponseDTO(
                 k.id,
                 k.maKhoa,
                 k.tenKhoa,
                 k.diaChi,
                 k.moTa
             )
             FROM Khoa k
             WHERE k.id = :khoaId
            """)
    KhoaAdminResponseDTO findByIdKhoaDTO(@Param("khoaId") UUID khoaId);

    @Query("""
             SELECT new com.university.dto.response.admin.KhoaAdminResponseDTO(
                 k.id,
                 k.maKhoa,
                 k.tenKhoa,
                 k.diaChi,
                 k.moTa
             )
             FROM Khoa k
             WHERE LOWER(k.tenKhoa) LIKE LOWER(CONCAT('%', :keyword , '%'))
            """)
    List<KhoaAdminResponseDTO> findByNameKhoaDTO(@Param("keyword") String keyword);

    void deleteAllByIdIn(List<UUID> ids);

}