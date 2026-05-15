package com.university.repository.student;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.dto.response.student.LopHocPhanStudentsResponseDTO;
import com.university.entity.LopHocPhan;
import jakarta.persistence.LockModeType;

@Repository
public interface LopHocPhanStudentsRepository extends JpaRepository<LopHocPhan, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT lhp
        FROM LopHocPhan lhp
        JOIN FETCH lhp.monHoc mh
        JOIN FETCH lhp.hocKi hk
        WHERE lhp.id = :lopHocPhanId
    """)
    Optional<LopHocPhan> findByIdForUpdate(@Param("lopHocPhanId") UUID lopHocPhanId);

    @Query(
        value = """
            SELECT new com.university.dto.response.student.LopHocPhanStudentsResponseDTO(
                lhp.id,
                lhp.maLopHocPhan,
                lhp.soLuongToiDa,
                (SELECT COUNT(d) FROM DangKyTinChi d WHERE d.lopHocPhan.id = lhp.id),
                lhp.hanDangKy,
                lhp.monHoc.id,
                lhp.monHoc.maMonHoc,
                lhp.monHoc.tenMonHoc,
                lhp.monHoc.soTinChi,
                lhp.hocKi.id,
                lhp.hocKi.maHocKi,
                lhp.hocKi.tenHocKi
            )
            FROM LopHocPhan lhp
            WHERE lhp.trangThai <> com.university.enums.TrangThaiLHP.DA_KET_THUC
            AND (:hocKiId IS NULL OR lhp.hocKi.id = :hocKiId)
            AND (:keyword = ''
                 OR LOWER(lhp.maLopHocPhan) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!'
                 OR LOWER(lhp.monHoc.tenMonHoc) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!')
            ORDER BY lhp.maLopHocPhan ASC
            """,
        countQuery = """
            SELECT COUNT(lhp)
            FROM LopHocPhan lhp
            WHERE lhp.trangThai <> com.university.enums.TrangThaiLHP.DA_KET_THUC
            AND (:hocKiId IS NULL OR lhp.hocKi.id = :hocKiId)
            AND (:keyword = ''
                 OR LOWER(lhp.maLopHocPhan) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!'
                 OR LOWER(lhp.monHoc.tenMonHoc) LIKE LOWER(CONCAT('%', :keyword, '%')) ESCAPE '!')
            """
    )
    Page<LopHocPhanStudentsResponseDTO> searchMoDangKy(
            @Param("hocKiId") UUID hocKiId,
            @Param("keyword") String keyword,
            Pageable pageable);
}
