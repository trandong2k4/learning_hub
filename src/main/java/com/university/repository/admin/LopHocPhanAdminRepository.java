package com.university.repository.admin;

import com.university.entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LopHocPhanAdminRepository extends JpaRepository<LopHocPhan, UUID> {
    void deleteAllByIdIn(List<UUID> ids);
}
