package com.university.repository.student;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.university.entity.HocVien;

import jakarta.persistence.LockModeType;

@Repository
public interface HocVienStudentsRepository extends JpaRepository<HocVien, UUID> {

    @Query("""
        SELECT h
        FROM HocVien h
        JOIN FETCH h.nganh n
        JOIN FETCH h.users u
        WHERE h.id = :hocVienId
    """)
    Optional<HocVien> findByIdWithNganh(@Param("hocVienId") UUID hocVienId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT h
        FROM HocVien h
        WHERE h.id = :hocVienId
    """)
    Optional<HocVien> findByIdForUpdate(@Param("hocVienId") UUID hocVienId);
}
