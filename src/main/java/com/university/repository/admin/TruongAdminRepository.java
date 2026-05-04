package com.university.repository.admin;

import com.university.dto.response.admin.TruongAdminResponseDTO;
import com.university.entity.Truong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TruongAdminRepository extends JpaRepository<Truong, UUID> {

    boolean existsByMaTruong(String maTruong);

    List<Truong> findByMaTruongIn(List<String> maTruongs);

    Truong findByMaTruong(String maTruongs);

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
             WHERE LOWER(t.tenTruong) LIKE LOWER(CONCAT('%',:keyword,'%'))
            """)
    List<TruongAdminResponseDTO> findTruongByTen(@Param("keyword") String keyword);

    void deleteAllByIdIn(List<UUID> ids);
}
