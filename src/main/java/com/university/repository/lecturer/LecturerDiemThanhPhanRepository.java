package com.university.repository.lecturer;

import com.university.entity.DiemThanhPhan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerDiemThanhPhanRepository extends JpaRepository<DiemThanhPhan, UUID> {
    List<DiemThanhPhan> findByDangKyTinChi_LopHocPhan_Id(UUID lopHocPhanId);
    Optional<DiemThanhPhan> findByDangKyTinChi_HocVien_IdAndCotDiem_Id(UUID hocVienId, UUID cotDiemId);

    @Query("SELECT d FROM DiemThanhPhan d JOIN FETCH d.dangKyTinChi dk JOIN FETCH dk.hocVien hv JOIN FETCH d.cotDiem WHERE dk.lopHocPhan.id = :lopHocPhanId")
    List<DiemThanhPhan> findByLopHocPhanIdWithRelations(@Param("lopHocPhanId") UUID lopHocPhanId);
}
