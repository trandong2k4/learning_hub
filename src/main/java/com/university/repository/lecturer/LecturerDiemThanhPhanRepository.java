package com.university.repository.lecturer;

import com.university.entity.DiemThanhPhan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerDiemThanhPhanRepository extends JpaRepository<DiemThanhPhan, UUID> {
    List<DiemThanhPhan> findByDangKyTinChi_LopHocPhan_Id(UUID lopHocPhanId);
    Optional<DiemThanhPhan> findByDangKyTinChi_HocVien_IdAndCotDiem_Id(UUID hocVienId, UUID cotDiemId);
}
