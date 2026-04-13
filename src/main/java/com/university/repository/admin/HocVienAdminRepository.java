package com.university.repository.admin;

import com.university.entity.HocVien;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HocVienAdminRepository extends JpaRepository<HocVien, UUID> {

}
