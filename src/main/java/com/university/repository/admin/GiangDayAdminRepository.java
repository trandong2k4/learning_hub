package com.university.repository.admin;

import com.university.entity.GiangDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GiangDayAdminRepository extends JpaRepository<GiangDay, UUID> {

    @Query("""
            SELECT DISTINCT gd FROM GiangDay gd
            JOIN FETCH gd.nhanVien nv
            JOIN FETCH nv.users u
            JOIN FETCH gd.lopHocPhan lhp
            JOIN FETCH lhp.monHoc mh
            JOIN FETCH lhp.hocKi hk
            """)
    List<GiangDay> findAllWithDetails();

    void deleteAllByIdIn(List<UUID> ids);

    boolean existsByNhanVienId(UUID nhanVienId);

    boolean existsByNhanVienIdAndLopHocPhanId(UUID nhanVienId, UUID lopHocPhanId);
}
