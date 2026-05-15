package com.university.repository.admin;

import com.university.dto.response.admin.LichSuLienHeAdminResponseDTO;
import com.university.entity.LichSuLienHe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LichSuLienHeAdminRepository extends JpaRepository<LichSuLienHe, UUID> {

    @Query("""
            SELECT new com.university.dto.response.admin.LichSuLienHeAdminResponseDTO(
                ls.id,
                ls.nguoiLienHe,
                ls.email,
                ls.soDienThoai,
                ls.ngayLienHe,
                lh.id,
                lh.tenLienHe,
                lh.email,
                lh.soDienThoai
            )
            FROM LichSuLienHe ls
            JOIN ls.lienHe lh
            ORDER BY ls.ngayLienHe DESC
            """)
    List<LichSuLienHeAdminResponseDTO> findAllDTO();

    @Query("""
            SELECT new com.university.dto.response.admin.LichSuLienHeAdminResponseDTO(
                ls.id,
                ls.nguoiLienHe,
                ls.email,
                ls.soDienThoai,
                ls.ngayLienHe,
                lh.id,
                lh.tenLienHe,
                lh.email,
                lh.soDienThoai
            )
            FROM LichSuLienHe ls
            JOIN ls.lienHe lh
            WHERE ls.id = :id
            """)
    LichSuLienHeAdminResponseDTO findDTOById(@Param("id") UUID id);

    @Query("""
            SELECT new com.university.dto.response.admin.LichSuLienHeAdminResponseDTO(
                ls.id,
                ls.nguoiLienHe,
                ls.email,
                ls.soDienThoai,
                ls.ngayLienHe,
                lh.id,
                lh.tenLienHe,
                lh.email,
                lh.soDienThoai
            )
            FROM LichSuLienHe ls
            JOIN ls.lienHe lh
            WHERE lh.id = :lienHeId
            ORDER BY ls.ngayLienHe DESC
            """)
    List<LichSuLienHeAdminResponseDTO> findAllByLienHeIdDTO(@Param("lienHeId") UUID lienHeId);

    @Query("""
            SELECT new com.university.dto.response.admin.LichSuLienHeAdminResponseDTO(
                ls.id,
                ls.nguoiLienHe,
                ls.email,
                ls.soDienThoai,
                ls.ngayLienHe,
                lh.id,
                lh.tenLienHe,
                lh.email,
                lh.soDienThoai
            )
            FROM LichSuLienHe ls
            JOIN ls.lienHe lh
            WHERE LOWER(ls.nguoiLienHe) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(ls.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY ls.ngayLienHe DESC
            """)
    List<LichSuLienHeAdminResponseDTO> searchDTO(@Param("keyword") String keyword);

    void deleteAllByIdIn(List<UUID> ids);
}
