package com.university.repository.student;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.university.entity.LopHocPhan;

import jakarta.persistence.LockModeType;

@Repository
public interface LopHocPhanStudentsRepository extends JpaRepository<LopHocPhan, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT lhp
        FROM LopHocPhan lhp
        JOIN FETCH lhp.monHoc mh
        WHERE lhp.id = :lopHocPhanId
    """)
    Optional<LopHocPhan> findByIdForUpdate(@Param("lopHocPhanId") UUID lopHocPhanId);
}
