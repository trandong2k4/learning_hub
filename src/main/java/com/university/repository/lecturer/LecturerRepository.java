package com.university.repository.lecturer;

import com.university.entity.NhanVien;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerRepository extends JpaRepository<NhanVien, UUID> {
    Optional<NhanVien> findByUsers_Id(UUID userId);
}
