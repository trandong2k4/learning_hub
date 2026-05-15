package com.university.repository.lecturer;

import com.university.entity.DangKyTinChi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LecturerDangKyTinChiRepository extends JpaRepository<DangKyTinChi, UUID> {

    List<DangKyTinChi> findByLopHocPhan_Id(UUID lopHocPhanId);

    Optional<DangKyTinChi> findByHocVien_IdAndLopHocPhan_Id(UUID hocVienId, UUID lopHocPhanId);

    @Query("SELECT d FROM DangKyTinChi d WHERE d.hocVien.id = :hocVienId AND d.lopHocPhan.id = :lopHocPhanId")
    Optional<DangKyTinChi> findByHocVien_IdAndLopHocPhan_IdQuery(@Param("hocVienId") UUID hocVienId, @Param("lopHocPhanId") UUID lopHocPhanId);

    long countByLopHocPhan_Id(UUID lopHocPhanId);
}