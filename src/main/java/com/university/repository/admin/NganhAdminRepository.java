package com.university.repository.admin;

import com.university.dto.response.admin.NganhAdminResponseDTO;
import com.university.entity.Nganh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NganhAdminRepository extends JpaRepository<Nganh, UUID> {
        boolean existsByMaNganh(String maNganh);

        boolean existsByKhoaId(UUID id);

        @Query("""
                        SELECT new com.university.dto.response.admin.NganhAdminResponseDTO(
                                n.id,
                                n.maNganh,
                                n.tenNganh,
                                n.danhGia,
                                n.moTa,
                                k.id,
                                k.tenKhoa
                            )
                            FROM Nganh n
                            JOIN n.khoa k
                            WHERE LOWER(n.tenNganh) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        """)
        List<NganhAdminResponseDTO> searchByTenNganh(@Param("keyword") String keyword);

        Nganh findByMaNganh(String maNganh);

        @Query("SELECT n.maNganh FROM Nganh n")
        List<String> findAllMaNganh();

        @Query("""
                        SELECT new com.university.dto.response.admin.NganhAdminResponseDTO(
                                        n.id,
                                        n.maNganh,
                                        n.tenNganh,
                                        n.danhGia,
                                        n.moTa,
                                        k.id,
                                        k.tenKhoa
                                        )
                                        FROM Nganh n
                                        JOIN n.khoa k
                        """)
        List<NganhAdminResponseDTO> getAllDTO();

        void deleteAllByIdIn(List<UUID> ids);
}
