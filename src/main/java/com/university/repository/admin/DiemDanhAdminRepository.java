package com.university.repository.admin;

import com.university.entity.DiemDanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface DiemDanhAdminRepository extends JpaRepository<DiemDanh, UUID> {
}
