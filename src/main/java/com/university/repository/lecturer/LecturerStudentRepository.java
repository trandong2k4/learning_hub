package com.university.repository.lecturer;

import com.university.dto.response.lecturer.LecturerClassStudentResponseDTO;
import com.university.entity.DangKyTinChi;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerStudentRepository extends JpaRepository<DangKyTinChi, UUID> {

    @Query("""
        SELECT new com.university.dto.response.lecturer.LecturerClassStudentResponseDTO(
            h.id,
            u.hoTen,
            h.maHocVien,
            ''
        )
        FROM DangKyTinChi d
        JOIN d.hocVien h
        JOIN h.users u
        WHERE d.lopHocPhan.id = :lopHocPhanId
          AND (
              LOWER(u.hoTen) LIKE LOWER(CONCAT('%', COALESCE(:keyword, ''), '%'))
              OR LOWER(h.maHocVien) LIKE LOWER(CONCAT('%', COALESCE(:keyword, ''), '%'))
          )
        ORDER BY u.hoTen
    """)
    List<LecturerClassStudentResponseDTO> findStudentsByLopHocPhanId(
            @Param("lopHocPhanId") UUID lopHocPhanId,
            @Param("keyword") String keyword);
}
