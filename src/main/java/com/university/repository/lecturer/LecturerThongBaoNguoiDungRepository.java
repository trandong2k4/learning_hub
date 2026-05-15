package com.university.repository.lecturer;

import com.university.entity.ThongBaoNguoiDung;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LecturerThongBaoNguoiDungRepository extends JpaRepository<ThongBaoNguoiDung, UUID> {

    @Query("""
            SELECT COUNT(tbnd) FROM ThongBaoNguoiDung tbnd
            WHERE tbnd.thongBao.id = :thongBaoId
            """)
    long countByThongBaoId(@Param("thongBaoId") UUID thongBaoId);

    @Query("""
            SELECT COUNT(tbnd) FROM ThongBaoNguoiDung tbnd
            WHERE tbnd.thongBao.id = :thongBaoId AND tbnd.daNhan = true
            """)
    long countReceivedByThongBaoId(@Param("thongBaoId") UUID thongBaoId);

    @Query("""
            SELECT tbnd FROM ThongBaoNguoiDung tbnd
            JOIN FETCH tbnd.thongBao tb
            JOIN FETCH tb.users
            WHERE tbnd.users.id = :userId
            ORDER BY tb.createdAt DESC
            """)
    List<ThongBaoNguoiDung> findRecentByUserId(@Param("userId") UUID userId, Pageable pageable);
}
