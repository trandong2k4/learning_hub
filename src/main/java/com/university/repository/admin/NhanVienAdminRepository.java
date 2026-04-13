package com.university.repository.admin;

import com.university.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface NhanVienAdminRepository extends JpaRepository<NhanVien, UUID> {

}
