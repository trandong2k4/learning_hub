package com.university.repository.student;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.university.dto.response.student.ThongBaoResponse;
import com.university.entity.ThongBaoNguoiDung;

public interface ThongBaoNguoiDungRepository extends JpaRepository<ThongBaoNguoiDung, UUID> {

    @Query("""
            SELECT new com.university.dto.response.student.ThongBaoResponse(
                t.id,
                t.tieuDe,
                t.noiDung,
                t.fileThongBao,
                t.loaiThongBao,
                t.createdAt,
                tnd.id,
                tnd.daNhan
            )
            FROM ThongBaoNguoiDung tnd
            JOIN tnd.thongBao t
            WHERE tnd.users.id = :usersId
            ORDER BY t.createdAt DESC
            """)
    List<ThongBaoResponse> findThongBaoByUsersId(@Param("usersId") UUID usersId);

    Optional<ThongBaoNguoiDung> findByIdAndUsersId(UUID id, UUID usersId);

    @Modifying
    @Query("""
            UPDATE ThongBaoNguoiDung tnd
            SET tnd.daNhan = true
            WHERE tnd.users.id = :usersId
              AND (tnd.daNhan = false OR tnd.daNhan IS NULL)
            """)
    void markAllAsReadByUsersId(@Param("usersId") UUID usersId);
}
