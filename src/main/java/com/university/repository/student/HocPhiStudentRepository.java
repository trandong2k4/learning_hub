package com.university.repository.student;

import com.university.entity.HocPhi;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HocPhiStudentRepository extends JpaRepository<HocPhi, UUID> {

    @Query("""
        SELECT hp
        FROM HocPhi hp
        JOIN FETCH hp.hocKi hk
        JOIN FETCH hp.hocVien hv
        JOIN FETCH hv.users u
        LEFT JOIN FETCH hp.thanhToanHocPhi tt
        WHERE hv.id = :hocVienId
        ORDER BY hk.ngayBatDau DESC, hp.createdAt DESC
    """)
    List<HocPhi> findAllByHocVienIdWithDetails(@Param("hocVienId") UUID hocVienId);

    @Query("""
        SELECT hp
        FROM HocPhi hp
        JOIN FETCH hp.hocKi hk
        JOIN FETCH hp.hocVien hv
        JOIN FETCH hv.users u
        LEFT JOIN FETCH hp.thanhToanHocPhi tt
        WHERE hp.id = :hocPhiId
        AND hv.id = :hocVienId
    """)
    Optional<HocPhi> findOwnedByHocVienId(@Param("hocPhiId") UUID hocPhiId, @Param("hocVienId") UUID hocVienId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT hp
        FROM HocPhi hp
        JOIN FETCH hp.hocKi hk
        JOIN FETCH hp.hocVien hv
        JOIN FETCH hv.users u
        LEFT JOIN FETCH hp.thanhToanHocPhi tt
        WHERE hp.id = :hocPhiId
        AND hv.id = :hocVienId
    """)
    Optional<HocPhi> findOwnedByHocVienIdForUpdate(@Param("hocPhiId") UUID hocPhiId,
                                                   @Param("hocVienId") UUID hocVienId);
}
