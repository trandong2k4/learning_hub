package com.university.repository.admin;

import com.university.dto.response.admin.TruongAdminResponseDTO;
import com.university.dto.response.admin.TruongAdminResponseDTO.TruongView;
import com.university.entity.Truong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TruongAdminRepository extends JpaRepository<Truong, UUID> {

    @Query("""
            SELECT COUNT(k) > 0
            FROM Khoa k
            WHERE k.truong.id = :truongId
            """)
    boolean existsKhoaByTruongId(UUID truongId);

    boolean existsByMaTruong(String maTruong);

    List<Truong> findByMaTruongIn(List<String> maTruongs);

    @Query("SELECT t.maTruong FROM Truong t")
    List<String> findAllMaTruong();

    @Query("""
             SELECT new com.university.dto.response.admin.TruongAdminResponseDTO(
                 t.id,
                 t.maTruong,
                 t.tenTruong,
                 t.diaChi,
                 t.moTa,
                 t.ngayThanhLap,
                 t.nguoiDaiDien
             )
             FROM Truong t
            """)
    List<TruongAdminResponseDTO> FindAllDTO();

    @Query("""
             SELECT new com.university.dto.response.admin.TruongAdminResponseDTO(
                 t.id,
                 t.maTruong,
                 t.tenTruong,
                 t.diaChi,
                 t.moTa,
                 t.ngayThanhLap,
                 t.nguoiDaiDien
             )
             FROM Truong t
             WHERE t.id = :truongId
            """)
    TruongAdminResponseDTO findTruongById(@Param("truongId") UUID truongId);

    @Query("""
             SELECT new com.university.dto.response.admin.TruongAdminResponseDTO(
                 t.id,
                 t.maTruong,
                 t.tenTruong,
                 t.diaChi,
                 t.moTa,
                 t.ngayThanhLap,
                 t.nguoiDaiDien
             )
             FROM Truong t
             WHERE LOWER(t.tenTruong) LIKE LOWER(CONCAT('%',:keyword,'%'))
            """)
    List<TruongAdminResponseDTO> findTruongByTen(@Param("keyword") String keyword);

    @Query("""
             SELECT
                 t.id as id,
                 t.maTruong as maTruong,
                 t.tenTruong as tenTruong
             FROM Truong t
             WHERE t.id = :truongId
            """)
    TruongView findTruongView(@Param("truongId") UUID truongId);

    @Query(" DELETE FROM Truong")
    void deleteAll();
}
