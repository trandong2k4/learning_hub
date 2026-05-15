package com.university.repository.admin;

import com.university.dto.response.admin.LienHeAdminResponseDTO;
import com.university.entity.LienHe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LienHeAdminRepository extends JpaRepository<LienHe, UUID> {

    boolean existsByTenLienHeIgnoreCase(String tenLienHe);

    boolean existsByTenLienHeIgnoreCaseAndIdNot(String tenLienHe, UUID id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);

    boolean existsBySoDienThoai(String soDienThoai);

    boolean existsBySoDienThoaiAndIdNot(String soDienThoai, UUID id);

    @Query("""
            SELECT new com.university.dto.response.admin.LienHeAdminResponseDTO(
                lh.id,
                lh.tenLienHe,
                lh.fanPageUrl,
                lh.email,
                lh.soDienThoai,
                k.id,
                k.maKhoa,
                k.tenKhoa
            )
            FROM LienHe lh
            JOIN lh.khoa k
            ORDER BY k.maKhoa ASC, lh.tenLienHe ASC
            """)
    List<LienHeAdminResponseDTO> findAllDTO();

    @Query("""
            SELECT new com.university.dto.response.admin.LienHeAdminResponseDTO(
                lh.id,
                lh.tenLienHe,
                lh.fanPageUrl,
                lh.email,
                lh.soDienThoai,
                k.id,
                k.maKhoa,
                k.tenKhoa
            )
            FROM LienHe lh
            JOIN lh.khoa k
            WHERE k.id = :khoaId
            ORDER BY lh.tenLienHe ASC
            """)
    List<LienHeAdminResponseDTO> findAllByKhoaIdDTO(@Param("khoaId") UUID khoaId);

    @Query("""
            SELECT new com.university.dto.response.admin.LienHeAdminResponseDTO(
                lh.id,
                lh.tenLienHe,
                lh.fanPageUrl,
                lh.email,
                lh.soDienThoai,
                k.id,
                k.maKhoa,
                k.tenKhoa
            )
            FROM LienHe lh
            JOIN lh.khoa k
            WHERE lh.id = :id
            """)
    LienHeAdminResponseDTO findDTOById(@Param("id") UUID id);

    @Query("""
            SELECT new com.university.dto.response.admin.LienHeAdminResponseDTO(
                lh.id,
                lh.tenLienHe,
                lh.fanPageUrl,
                lh.email,
                lh.soDienThoai,
                k.id,
                k.maKhoa,
                k.tenKhoa
            )
            FROM LienHe lh
            JOIN lh.khoa k
            WHERE LOWER(lh.tenLienHe) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(lh.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR lh.soDienThoai LIKE CONCAT('%', :keyword, '%')
            ORDER BY k.maKhoa ASC, lh.tenLienHe ASC
            """)
    List<LienHeAdminResponseDTO> searchDTO(@Param("keyword") String keyword);

    void deleteAllByIdIn(List<UUID> ids);
}
