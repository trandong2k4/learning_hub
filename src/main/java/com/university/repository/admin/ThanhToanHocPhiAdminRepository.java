package com.university.repository.admin;

import com.university.entity.ThanhToanHocPhi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ThanhToanHocPhiAdminRepository extends JpaRepository<ThanhToanHocPhi, UUID> {

}
