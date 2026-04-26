package com.university.repository.admin;

import com.university.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface NhanVienAdminRepository extends JpaRepository<NhanVien, UUID> {
    @Query("""
                SELECT COUNT(DISTINCT nv)
                FROM NhanVien nv
                JOIN nv.users us
                JOIN us.dUserRoles ur
                JOIN ur.role r
                WHERE r.maRole = 'LECTURE'
            """)
    Long countGiangVien();
}
