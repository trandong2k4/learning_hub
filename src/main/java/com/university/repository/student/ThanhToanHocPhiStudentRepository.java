package com.university.repository.student;

import com.university.entity.ThanhToanHocPhi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ThanhToanHocPhiStudentRepository extends JpaRepository<ThanhToanHocPhi, UUID> {
}
