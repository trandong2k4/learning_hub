package com.university.repository.lecturer;

import com.university.entity.CotDiem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerGradeRepository extends JpaRepository<CotDiem, UUID> {
    List<CotDiem> findByLopHocPhan_Id(UUID lopHocPhanId);
    Optional<CotDiem> findByLopHocPhan_IdAndTenCotDiem(UUID lopHocPhanId, String tenCotDiem);
}
