package com.university.repository.admin;

import com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO;
import com.university.entity.PhanHoiLienHe;
import com.university.enums.TrangThaiXuLyLienHeEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PhanHoiLienHeAdminRepository extends JpaRepository<PhanHoiLienHe, UUID> {

    @Query("""
            SELECT new com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO(
                p.id,
                p.hoTen,
                p.email,
                p.soDienThoai,
                p.chuDe,
                p.noiDung,
                p.trangThai,
                p.gioiTinh,
                p.nguoiXuLy,
                p.ngayTao,
                p.ngayCapNhat,
                null
            )
            FROM PhanHoiLienHe p
            ORDER BY p.ngayTao DESC
            """)
    List<PhanHoiLienHeAdminResponseDTO> findAllDTO();

    @Query("""
            SELECT new com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO(
                p.id,
                p.hoTen,
                p.email,
                p.soDienThoai,
                p.chuDe,
                p.noiDung,
                p.trangThai,
                p.gioiTinh,
                p.nguoiXuLy,
                p.ngayTao,
                p.ngayCapNhat,
                null
            )
            FROM PhanHoiLienHe p
            WHERE p.trangThai = :trangThai
            ORDER BY p.ngayTao DESC
            """)
    List<PhanHoiLienHeAdminResponseDTO> findAllByTrangThaiDTO(@Param("trangThai") TrangThaiXuLyLienHeEnum trangThai);

    @Query("""
            SELECT new com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO(
                p.id,
                p.hoTen,
                p.email,
                p.soDienThoai,
                p.chuDe,
                p.noiDung,
                p.trangThai,
                p.gioiTinh,
                p.nguoiXuLy,
                p.ngayTao,
                p.ngayCapNhat,
                null
            )
            FROM PhanHoiLienHe p
            WHERE LOWER(p.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.chuDe) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY p.ngayTao DESC
            """)
    List<PhanHoiLienHeAdminResponseDTO> searchByKeywordDTO(@Param("keyword") String keyword);

    @Query("""
            SELECT new com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO(
                p.id,
                p.hoTen,
                p.email,
                p.soDienThoai,
                p.chuDe,
                p.noiDung,
                p.trangThai,
                p.gioiTinh,
                p.nguoiXuLy,
                p.ngayTao,
                p.ngayCapNhat,
                null
            )
            FROM PhanHoiLienHe p
            WHERE (LOWER(p.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.chuDe) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND p.trangThai = :trangThai
            ORDER BY p.ngayTao DESC
            """)
    List<PhanHoiLienHeAdminResponseDTO> searchByKeywordAndTrangThaiDTO(
            @Param("keyword") String keyword,
            @Param("trangThai") TrangThaiXuLyLienHeEnum trangThai);

    @Query("""
            SELECT new com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO(
                p.id,
                p.hoTen,
                p.email,
                p.soDienThoai,
                p.chuDe,
                p.noiDung,
                p.trangThai,
                p.gioiTinh,
                p.nguoiXuLy,
                p.ngayTao,
                p.ngayCapNhat,
                null
            )
            FROM PhanHoiLienHe p
            WHERE p.ngayTao BETWEEN :startDate AND :endDate
            ORDER BY p.ngayTao DESC
            """)
    List<PhanHoiLienHeAdminResponseDTO> findByDateRangeDTO(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT new com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO(
                p.id,
                p.hoTen,
                p.email,
                p.soDienThoai,
                p.chuDe,
                p.noiDung,
                p.trangThai,
                p.gioiTinh,
                p.nguoiXuLy,
                p.ngayTao,
                p.ngayCapNhat,
                null
            )
            FROM PhanHoiLienHe p
            WHERE p.id = :id
            """)
    PhanHoiLienHeAdminResponseDTO findDTOById(@Param("id") UUID id);

    @Query("SELECT COUNT(p) FROM PhanHoiLienHe p WHERE p.trangThai = :trangThai")
    long countByTrangThai(@Param("trangThai") TrangThaiXuLyLienHeEnum trangThai);

    void deleteAllByIdIn(List<UUID> ids);
}
