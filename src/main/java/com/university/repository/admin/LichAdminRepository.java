package com.university.repository.admin;

import com.university.entity.Lich;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LichAdminRepository extends JpaRepository<Lich, UUID> {

    @Query("SELECT l FROM Lich l LEFT JOIN FETCH l.gioHoc LEFT JOIN FETCH l.phong LEFT JOIN FETCH l.lopHocPhan LEFT JOIN FETCH l.lopHocPhan.monHoc LEFT JOIN FETCH l.lopHocPhan.hocKi WHERE l.lopHocPhan.id = :lopHocPhanId")
    List<Lich> findAllLichByLopHocPhanId(@Param("lopHocPhanId") UUID lopHocPhanId);

    @Query("SELECT l FROM Lich l LEFT JOIN FETCH l.gioHoc LEFT JOIN FETCH l.phong LEFT JOIN FETCH l.lopHocPhan LEFT JOIN FETCH l.lopHocPhan.monHoc LEFT JOIN FETCH l.lopHocPhan.hocKi")
    List<Lich> findAllWithDetails();

    boolean existsByPhongId(UUID phongId);

    boolean existsByGioHocId(UUID gioHocId);

    List<Lich> findAllByGioHocId(UUID gioHocId);

    @Query("SELECT l FROM Lich l LEFT JOIN FETCH l.gioHoc LEFT JOIN FETCH l.phong LEFT JOIN FETCH l.lopHocPhan LEFT JOIN FETCH l.lopHocPhan.monHoc LEFT JOIN FETCH l.lopHocPhan.hocKi WHERE l.id = :id")
    Optional<Lich> findByIdWithDetails(@Param("id") UUID id);

    @Query("""
            SELECT l FROM Lich l
            JOIN FETCH l.gioHoc gh
            JOIN FETCH l.phong p
            JOIN FETCH l.lopHocPhan lhp
            LEFT JOIN FETCH lhp.monHoc
            LEFT JOIN FETCH lhp.hocKi
            WHERE p.id = :phongId
              AND gh.id = :gioHocId
              AND l.ngayHoc >= :startOfDay
              AND l.ngayHoc < :startOfNextDay
            """)
    List<Lich> findRoomScheduleConflicts(
            @Param("phongId") UUID phongId,
            @Param("gioHocId") UUID gioHocId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay);

    @Query("""
            SELECT l FROM Lich l
            JOIN FETCH l.gioHoc gh
            JOIN FETCH l.phong p
            JOIN FETCH l.lopHocPhan lhp
            LEFT JOIN FETCH lhp.monHoc
            LEFT JOIN FETCH lhp.hocKi
            WHERE p.id = :phongId
              AND gh.id = :gioHocId
              AND l.ngayHoc >= :startOfDay
              AND l.ngayHoc < :startOfNextDay
              AND l.id <> :excludeId
            """)
    List<Lich> findRoomScheduleConflictsExcluding(
            @Param("phongId") UUID phongId,
            @Param("gioHocId") UUID gioHocId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay,
            @Param("excludeId") UUID excludeId);

    @Modifying
    @Query("DELETE FROM Lich l WHERE l.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<UUID> ids);

    @Query("""
            SELECT DISTINCT l FROM Lich l
            LEFT JOIN FETCH l.gioHoc
            LEFT JOIN FETCH l.phong
            LEFT JOIN FETCH l.lopHocPhan lhp
            LEFT JOIN FETCH lhp.monHoc
            LEFT JOIN FETCH lhp.hocKi
            WHERE lhp.id IN (
                SELECT gd.lopHocPhan.id FROM GiangDay gd WHERE gd.nhanVien.id = :nhanVienId
            )
            """)
    List<Lich> findAllByNhanVienId(@Param("nhanVienId") UUID nhanVienId);
}
