package com.university.repository.lecturer;

import com.university.entity.TaiLieu;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerDocumentRepository extends JpaRepository<TaiLieu, UUID> {
    List<TaiLieu> findByLopHocPhan_Id(UUID lopHocPhanId);
}
