package com.university.repository.student;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.university.dto.response.student.TaiLieuStudentsResponseDTO;
import com.university.entity.TaiLieu;
import com.university.enums.TaiLieuEnum;

public interface TaiLieuStudentsRepository extends JpaRepository<TaiLieu, UUID> {

    @Query(
        value = """
            SELECT new com.university.dto.response.student.TaiLieuStudentsResponseDTO(
                t.id,
                t.tenTaiLieu,
                t.moTa,
                t.fileTaiLieuUrl,
                t.loaiTaiLieu,
                t.ngayDang,
                t.lopHocPhan.id
            )
            FROM TaiLieu t
            WHERE t.lopHocPhan.id = :lophocphanId
            ORDER BY t.ngayDang DESC
            """,
        countQuery = """
            SELECT COUNT(t)
            FROM TaiLieu t
            WHERE t.lopHocPhan.id = :lophocphanId
            """
    )
    Page<TaiLieuStudentsResponseDTO> findByLopHocPhanId(
            @Param("lophocphanId") UUID lophocphanId,
            Pageable pageable);

    @Query(
        value = """
            SELECT new com.university.dto.response.student.TaiLieuStudentsResponseDTO(
                t.id,
                t.tenTaiLieu,
                t.moTa,
                t.fileTaiLieuUrl,
                t.loaiTaiLieu,
                t.ngayDang,
                t.lopHocPhan.id
            )
            FROM TaiLieu t
            WHERE t.lopHocPhan.id = :lophocphanId
            AND (:keyword = '' OR LOWER(t.tenTaiLieu) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!')
            AND (:loaiTaiLieu IS NULL OR t.loaiTaiLieu = :loaiTaiLieu)
            ORDER BY t.ngayDang DESC
            """,
        countQuery = """
            SELECT COUNT(t)
            FROM TaiLieu t
            WHERE t.lopHocPhan.id = :lophocphanId
            AND (:keyword = '' OR LOWER(t.tenTaiLieu) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!')
            AND (:loaiTaiLieu IS NULL OR t.loaiTaiLieu = :loaiTaiLieu)
            """
    )
    Page<TaiLieuStudentsResponseDTO> searchTaiLieu(
            @Param("lophocphanId") UUID lophocphanId,
            @Param("keyword") String keyword,
            @Param("loaiTaiLieu") TaiLieuEnum loaiTaiLieu,
            Pageable pageable);
}
