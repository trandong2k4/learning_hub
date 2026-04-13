package com.university.repository.student;

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
    Optional<ThongBaoNguoiDung> findByUsersIdAndThongBaoId(UUID usersId, UUID thongBaoId);
    
    @Query("""
        SELECT tnd
        FROM ThongBaoNguoiDung tnd
        WHERE tnd.users.id = :usersId AND tnd.daNhan = false
    """)
    List<ThongBaoNguoiDung> findByUsersIdAndDaNhanFalse(@Param("usersId") UUID usersId);
}
