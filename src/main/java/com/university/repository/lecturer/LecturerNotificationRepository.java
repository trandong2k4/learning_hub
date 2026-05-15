package com.university.repository.lecturer;

import com.university.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface LecturerNotificationRepository extends JpaRepository<ThongBao, UUID> {

    @Query("""
            SELECT tb FROM ThongBao tb
            JOIN tb.users u
            WHERE u.id = :userId
            ORDER BY tb.createdAt DESC
            """)
    List<ThongBao> findByUsers_IdOrderByCreatedAtDesc(@Param("userId") UUID userId);
}
