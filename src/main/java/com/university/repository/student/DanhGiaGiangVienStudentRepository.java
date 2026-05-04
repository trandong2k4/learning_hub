package com.university.repository.student;

import com.university.entity.DanhGiaGiangVien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DanhGiaGiangVienStudentRepository extends JpaRepository<DanhGiaGiangVien, UUID> {
}
