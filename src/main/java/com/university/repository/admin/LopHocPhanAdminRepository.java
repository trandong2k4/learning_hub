package com.university.repository.admin;

import com.university.entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface LopHocPhanAdminRepository extends JpaRepository<LopHocPhan, UUID> {

}
